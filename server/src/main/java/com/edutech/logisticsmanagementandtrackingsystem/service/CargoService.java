package com.edutech.logisticsmanagementandtrackingsystem.service;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.edutech.logisticsmanagementandtrackingsystem.dto.CargoStatusResponse;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;
import com.edutech.logisticsmanagementandtrackingsystem.entity.CargoDocument;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Driver;
import com.edutech.logisticsmanagementandtrackingsystem.repository.CargoDocumentRepository;
import com.edutech.logisticsmanagementandtrackingsystem.repository.CargoRepository;
import com.edutech.logisticsmanagementandtrackingsystem.repository.DriverRepository;

@Service
@Transactional
public class CargoService {

    private static final Logger logger = LoggerFactory.getLogger(CargoService.class);

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CargoDocumentRepository cargoDocumentRepository;

    @Autowired
    private DocumentStorageService documentStorageService;

    // ✅ 11-digit AWB from id (keeps leading zeros)
    private String generateAwbFromId(Long id) {
        if (id == null) return null;
        return String.format("%011d", id);
    }

    // =========================
    // ADD CARGO
    // =========================
    public Cargo addCargo(Cargo cargo) {

        if (cargo == null) {
            logger.warn("CARGO-SERVICE: addCargo called with null cargo");
            return null;
        }

        logger.info("CARGO-SERVICE: Creating cargo request received");

        Cargo saved = cargoRepository.save(cargo);

        // ✅ Set AWB after ID is generated
        if (saved != null && (saved.getAwb() == null || saved.getAwb().trim().isEmpty())) {
            saved.setAwb(generateAwbFromId(saved.getId()));
            saved = cargoRepository.save(saved);
        }

        logger.info("CARGO-SERVICE: Cargo created successfully | cargoId={} | awb={}",
                saved != null ? saved.getId() : null,
                saved != null ? saved.getAwb() : null);

        return saved;
    }

    // =========================
    // VIEW ALL CARGO
    // =========================
    public List<Cargo> viewAllCargo() {
        logger.info("CARGO-SERVICE: Fetch all cargo request received");
        return cargoRepository.findAll();
    }

    // =========================
    // ASSIGN CARGO TO DRIVER
    // =========================
    public boolean assignCargoToDriver(long cargoId, Long driverId) {

        logger.info("CARGO-SERVICE: Assign cargo request | cargoId={} | driverId={}", cargoId, driverId);

        Cargo cargo = cargoRepository.findById(cargoId)
                .orElseThrow(() -> new EntityNotFoundException("Cargo not found"));

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        cargo.setDriver(driver);

        // ✅ Optional: update status from pending to assigned
        if (cargo.getStatus() != null && cargo.getStatus().equalsIgnoreCase("Order Pending")) {
            cargo.setStatus("Order Assigned");
        }

        cargoRepository.save(cargo);

        logger.info("CARGO-SERVICE: Cargo assigned successfully | cargoId={} | awb={} | driverId={}",
                cargo.getId(), cargo.getAwb(), driverId);

        return true;
    }

    // =========================
    // UPDATE CARGO STATUS
    // =========================
    public boolean updateCargoStatus(Long cargoId, String newStatus) {

        logger.info("CARGO-SERVICE: Update cargo status | cargoId={} | newStatus={}", cargoId, newStatus);

        if (cargoId == null || newStatus == null || newStatus.trim().isEmpty()) {
            logger.warn("CARGO-SERVICE: updateCargoStatus blocked due to invalid inputs");
            return false;
        }

        Cargo cargo = cargoRepository.findById(cargoId)
                .orElseThrow(() -> new EntityNotFoundException("Cargo not found"));

        cargo.setStatus(newStatus.trim());
        cargoRepository.save(cargo);

        return true;
    }

    // =========================
    // GET CARGO BY ID
    // =========================
    public Cargo getCargoById(long cargoId) {
        return cargoRepository.findById(cargoId).orElse(null);
    }

    // =========================
    // ✅ GET CARGO BY AWB
    // =========================
    public Cargo getCargoByAwb(String awb) {
        if (awb == null || awb.trim().isEmpty()) return null;
        return cargoRepository.findByAwb(awb.trim()).orElse(null);
    }

    // =========================
    // GET CARGO DETAILS (DTO) BY ID
    // =========================
    public CargoStatusResponse getCargoDetails(Long cargoId) {

        Cargo cargo = cargoRepository.findById(cargoId)
                .orElseThrow(() -> new EntityNotFoundException("Cargo not found"));

        CargoStatusResponse dto = new CargoStatusResponse();
        dto.setCargoId(cargo.getId());
        dto.setStatus(cargo.getStatus());
        dto.setSource(cargo.getSource());
        dto.setCargoContent(cargo.getCargoContent());
        dto.setCargoSize(cargo.getCargoSize());
        dto.setDriverId(cargo.getDriver() != null ? cargo.getDriver().getId() : null);

        return dto;
    }

    // =========================
    // ✅ GET CARGO DETAILS (DTO) BY AWB
    // =========================
    public CargoStatusResponse getCargoDetailsByAwb(String awb) {

        logger.info("CARGO-SERVICE: Get cargo details by AWB | awb={}", awb);

        if (awb == null || awb.trim().isEmpty()) {
            throw new EntityNotFoundException("Cargo not found");
        }

        Cargo cargo = cargoRepository.findByAwb(awb.trim())
                .orElseThrow(() -> new EntityNotFoundException("Cargo not found"));

        CargoStatusResponse dto = new CargoStatusResponse();
        dto.setCargoId(cargo.getId());
        dto.setStatus(cargo.getStatus());
        dto.setSource(cargo.getSource());
        dto.setCargoContent(cargo.getCargoContent());
        dto.setCargoSize(cargo.getCargoSize());
        dto.setDriverId(cargo.getDriver() != null ? cargo.getDriver().getId() : null);

        return dto;
    }

    // =========================
    // CREATE CARGO WITH DOCUMENTS (✅ DOCS MANDATORY)
    // =========================
    public Cargo createCargoWithDocuments(Cargo cargo, MultipartFile[] documents) {

        if (cargo == null) {
            logger.warn("CARGO-SERVICE: createCargoWithDocuments called with null cargo");
            throw new RuntimeException("Cargo is required");
        }

        // ✅ mandatory documents
        if (documents == null || documents.length == 0) {
            logger.warn("CARGO-SERVICE: createCargoWithDocuments blocked - documents missing");
            throw new RuntimeException("Documents are required");
        }

        logger.info("CARGO-SERVICE: Create cargo with documents | docsCount={}", documents.length);

        Cargo savedCargo = cargoRepository.save(cargo);

        // ✅ Set AWB after insert
        if (savedCargo.getAwb() == null || savedCargo.getAwb().trim().isEmpty()) {
            savedCargo.setAwb(generateAwbFromId(savedCargo.getId()));
            savedCargo = cargoRepository.save(savedCargo);
        }

        for (MultipartFile file : documents) {
            if (file == null || file.isEmpty()) {
                logger.warn("CARGO-SERVICE: Empty file skipped | cargoId={}", savedCargo.getId());
                continue;
            }

            try {
                CargoDocument doc = documentStorageService.storeFile(file, savedCargo);
                cargoDocumentRepository.save(doc);

                logger.info("CARGO-SERVICE: Document saved | cargoId={} | awb={} | fileName={}",
                        savedCargo.getId(), savedCargo.getAwb(), file.getOriginalFilename());

            } catch (IOException ex) {
                logger.error("CARGO-SERVICE: Document upload failed | cargoId={} | Reason={}",
                        savedCargo.getId(), ex.getMessage(), ex);
                throw new RuntimeException("Failed to upload cargo document", ex);
            }
        }

        return savedCargo;
    }
}