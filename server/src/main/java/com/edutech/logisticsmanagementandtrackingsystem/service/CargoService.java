package com.edutech.logisticsmanagementandtrackingsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;
import com.edutech.logisticsmanagementandtrackingsystem.entity.CargoDocument;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Driver;

import com.edutech.logisticsmanagementandtrackingsystem.repository.CargoDocumentRepository;
import com.edutech.logisticsmanagementandtrackingsystem.repository.CargoRepository;
import com.edutech.logisticsmanagementandtrackingsystem.repository.DriverRepository;

import javax.persistence.EntityNotFoundException;

import java.util.List;

@Service
@Transactional
public class CargoService {

    private static final Logger logger = LoggerFactory.getLogger(CargoService.class);

    @Autowired
    CargoRepository cargoRepository;

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    private CargoDocumentRepository cargoDocumentRepository;

    @Autowired
    private DocumentStorageService documentStorageService;

    // =========================
    // ADD CARGO
    // =========================
    public Cargo addCargo(Cargo cargo) {

        if (cargo == null) {
            logger.warn("CARGO-SERVICE: addCargo called with null cargo");
            return null;
        }

        logger.info("CARGO-SERVICE: Creating cargo request received");

        try {
            Cargo saved = cargoRepository.save(cargo);
            logger.info("CARGO-SERVICE: Cargo created successfully | cargoId={}",
                    saved != null ? saved.getId() : null);
            return saved;
        } catch (Exception ex) {
            logger.error("CARGO-SERVICE: Failed to create cargo | Reason={}", ex.getMessage(), ex);
            throw ex;
        }
    }

    // =========================
    // VIEW ALL CARGO
    // =========================
    public List<Cargo> viewAllCargo() {
        logger.info("CARGO-SERVICE: Fetch all cargo request received");
        try {
            List<Cargo> list = cargoRepository.findAll();
            logger.info("CARGO-SERVICE: Cargo fetched successfully | count={}", list != null ? list.size() : 0);
            return list;
        } catch (Exception ex) {
            logger.error("CARGO-SERVICE: Failed to fetch cargo list | Reason={}", ex.getMessage(), ex);
            throw ex;
        }
    }

    // =========================
    // ASSIGN CARGO TO DRIVER
    // =========================
    public boolean assignCargoToDriver(long cargoId, Long driverId) {

        logger.info("CARGO-SERVICE: Assign cargo request | cargoId={} | driverId={}", cargoId, driverId);

        try {
            Cargo cargo = cargoRepository.findById(cargoId)
                    .orElseThrow(() -> new EntityNotFoundException("Cargo not found"));

            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

            Long oldDriverId = (cargo.getDriver() != null) ? cargo.getDriver().getId() : null;

            cargo.setDriver(driver);
            cargoRepository.save(cargo);

            logger.info("CARGO-SERVICE: Cargo assigned successfully | cargoId={} | oldDriverId={} | newDriverId={}",
                    cargoId, oldDriverId, driverId);

            return true;

        } catch (EntityNotFoundException ex) {
            logger.warn("CARGO-SERVICE: Assign cargo failed | cargoId={} | driverId={} | Reason={}",
                    cargoId, driverId, ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            logger.error("CARGO-SERVICE: Error assigning cargo | cargoId={} | driverId={} | Reason={}",
                    cargoId, driverId, ex.getMessage(), ex);
            throw ex;
        }
    }

    // =========================
    // UPDATE CARGO STATUS
    // (Pending / Transit / Delivered)
    // =========================
    public boolean updateCargoStatus(Long cargoId, String newStatus) {

        logger.info("CARGO-SERVICE: Update status request | cargoId={} | newStatus={}", cargoId, newStatus);

        if (cargoId == null || newStatus == null || newStatus.trim().isEmpty()) {
            logger.warn("CARGO-SERVICE: Update status blocked due to invalid input | cargoId={} | newStatus={}",
                    cargoId, newStatus);
            return false;
        }

        try {
            Cargo cargo = cargoRepository.findById(cargoId)
                    .orElseThrow(() -> new EntityNotFoundException("Cargo not found"));

            String oldStatus = cargo.getStatus();
            String normalizedStatus = newStatus.trim().toUpperCase();

            // OPTIONAL validation for common statuses:
            // If you want strict validation, uncomment below.
            /*
            if (!(normalizedStatus.equals("PENDING") || normalizedStatus.equals("IN_TRANSIT") || normalizedStatus.equals("DELIVERED"))) {
                logger.warn("CARGO-SERVICE: Invalid status update attempted | cargoId={} | oldStatus={} | newStatus={}",
                        cargoId, oldStatus, normalizedStatus);
                return false;
            }
            */

            cargo.setStatus(normalizedStatus);
            cargoRepository.save(cargo);

            logger.info("CARGO-SERVICE: Cargo status changed | cargoId={} | oldStatus={} | newStatus={}",
                    cargoId, oldStatus, normalizedStatus);

            // Special business event log:
            if ("DELIVERED".equalsIgnoreCase(normalizedStatus)) {
                logger.info("✅ DELIVERY EVENT: Cargo delivered successfully | cargoId={}", cargoId);
            } else if ("IN_TRANSIT".equalsIgnoreCase(normalizedStatus)) {
                logger.info("🚚 TRANSIT EVENT: Cargo is now in transit | cargoId={}", cargoId);
            } else if ("PENDING".equalsIgnoreCase(normalizedStatus)) {
                logger.info("🕒 PENDING EVENT: Cargo marked as pending | cargoId={}", cargoId);
            }

            return true;

        } catch (EntityNotFoundException ex) {
            logger.warn("CARGO-SERVICE: Update status failed (not found) | cargoId={} | Reason={}",
                    cargoId, ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            logger.error("CARGO-SERVICE: Error updating status | cargoId={} | newStatus={} | Reason={}",
                    cargoId, newStatus, ex.getMessage(), ex);
            throw ex;
        }
    }

    // =========================
    // GET CARGO BY ID
    // =========================
    public Cargo getCargoById(long cargoId) {
        logger.info("CARGO-SERVICE: Get cargo by id request | cargoId={}", cargoId);

        try {
            Cargo cargo = cargoRepository.findById(cargoId).orElse(null);

            if (cargo == null) {
                logger.warn("CARGO-SERVICE: Cargo not found | cargoId={}", cargoId);
            } else {
                logger.info("CARGO-SERVICE: Cargo found | cargoId={} | status={}", cargoId, cargo.getStatus());
            }

            return cargo;

        } catch (Exception ex) {
            logger.error("CARGO-SERVICE: Error fetching cargo | cargoId={} | Reason={}", cargoId, ex.getMessage(), ex);
            throw ex;
        }
    }

    // =========================
    // CREATE CARGO WITH DOCUMENTS
    // =========================
    public Cargo createCargoWithDocuments(Cargo cargo, MultipartFile[] documents) {

        if (cargo == null) {
            logger.warn("CARGO-SERVICE: createCargoWithDocuments called with null cargo");
            return null;
        }

        int docCount = (documents != null) ? documents.length : 0;
        logger.info("CARGO-SERVICE: Create cargo with documents request | docsCount={}", docCount);

        Cargo savedCargo;
        try {
            savedCargo = cargoRepository.save(cargo);
            logger.info("CARGO-SERVICE: Cargo saved (for documents) | cargoId={}", savedCargo.getId());
        } catch (Exception ex) {
            logger.error("CARGO-SERVICE: Failed to save cargo before uploading documents | Reason={}",
                    ex.getMessage(), ex);
            throw ex;
        }

        if (documents != null) {
            for (MultipartFile file : documents) {
                try {
                    String originalName = (file != null) ? file.getOriginalFilename() : null;
                    logger.info("CARGO-SERVICE: Uploading document | cargoId={} | fileName={}",
                            savedCargo.getId(), originalName);

                    CargoDocument doc = documentStorageService.storeFile(file, savedCargo);
                    cargoDocumentRepository.save(doc);

                    logger.info("CARGO-SERVICE: Document saved successfully | cargoId={} | documentId={} | fileName={}",
                            savedCargo.getId(), doc != null ? doc.getId() : null, originalName);

                } catch (Exception ex) {
                    logger.error("CARGO-SERVICE: Document upload failed | cargoId={} | Reason={}",
                            savedCargo.getId(), ex.getMessage(), ex);
                    throw new RuntimeException("Failed to upload document", ex);
                }
            }
        } else {
            logger.info("CARGO-SERVICE: No documents provided | cargoId={}", savedCargo.getId());
        }

        return savedCargo;
    }
}