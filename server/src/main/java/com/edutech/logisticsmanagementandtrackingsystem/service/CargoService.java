package com.edutech.logisticsmanagementandtrackingsystem.service;

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

import javax.persistence.EntityNotFoundException;

import java.util.List;

@Service
@Transactional
public class CargoService {

@Autowired
private CargoRepository cargoRepository;

@Autowired
private DriverRepository driverRepository;

@Autowired
private CargoDocumentRepository cargoDocumentRepository;

@Autowired
private DocumentStorageService documentStorageService;

// :white_check_mark: Add Cargo
public Cargo addCargo(Cargo cargo) {
return cargoRepository.save(cargo);
}

// :white_check_mark: View All Cargo
public List<Cargo> viewAllCargo() {
return cargoRepository.findAll();
}

// :white_check_mark: Assign Driver
public boolean assignCargoToDriver(long cargoId, Long driverId) {
Cargo cargo = cargoRepository.findById(cargoId)
.orElseThrow(() -> new EntityNotFoundException("Cargo not found"));

Driver driver = driverRepository.findById(driverId)
.orElseThrow(() -> new EntityNotFoundException("Driver not found"));

cargo.setDriver(driver);
cargoRepository.save(cargo);
return true;
}

// :white_check_mark: Update Status
public boolean updateCargoStatus(Long cargoId, String newStatus) {
Cargo cargo = cargoRepository.findById(cargoId)
.orElseThrow(() -> new EntityNotFoundException("Cargo not found"));

cargo.setStatus(newStatus.toUpperCase());
cargoRepository.save(cargo);
return true;
}

// :x: OLD (keep if used elsewhere)
public Cargo getCargoById(long cargoId) {
return cargoRepository.findById(cargoId).orElse(null);
}

// :white_check_mark: :white_check_mark: NEW METHOD (IMPORTANT)
public CargoStatusResponse getCargoDetails(Long cargoId) {

Cargo cargo = cargoRepository.findById(cargoId)
.orElseThrow(() -> new EntityNotFoundException("Cargo not found"));

CargoStatusResponse dto = new CargoStatusResponse();

dto.setCargoId(cargo.getId());
dto.setStatus(cargo.getStatus());
dto.setSource(cargo.getSource());

dto.setCargoContent(cargo.getCargoContent());
dto.setCargoSize(cargo.getCargoSize());

// :white_check_mark: DRIVER FIX
if (cargo.getDriver() != null) {
dto.setDriverId(cargo.getDriver().getId());
} else {
dto.setDriverId(null);
}

return dto;
}

// :white_check_mark: Upload Documents
public Cargo createCargoWithDocuments(Cargo cargo, MultipartFile[] documents) {

Cargo savedCargo = cargoRepository.save(cargo);

if (documents != null) {
for (MultipartFile file : documents) {
try {
CargoDocument doc = documentStorageService.storeFile(file, savedCargo);
cargoDocumentRepository.save(doc);
} catch (Exception e) {
throw new RuntimeException("Failed to upload document", e);
}
}
}
return savedCargo;
}
}