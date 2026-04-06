package com.edutech.logisticsmanagementandtrackingsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Driver;
import com.edutech.logisticsmanagementandtrackingsystem.repository.CargoRepository;
import com.edutech.logisticsmanagementandtrackingsystem.repository.DriverRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class DriverService {

    private static final Logger logger = LoggerFactory.getLogger(DriverService.class);

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CargoRepository cargoRepository;

    // =========================
    // CREATE DRIVER PROFILE
    // =========================
    public Driver createDriver(Driver driver) {

        if (driver == null) {
            logger.warn("DRIVER-SERVICE: createDriver called with null driver");
            return null;
        }

        logger.info("DRIVER-SERVICE: Creating driver profile | name={} | email={}",
                driver.getName(), driver.getEmail());

        try {
            Driver saved = driverRepository.save(driver);
            logger.info("DRIVER-SERVICE: Driver profile created successfully | driverId={}",
                    saved != null ? saved.getId() : null);
            return saved;

        } catch (Exception ex) {
            logger.error("DRIVER-SERVICE: Failed to create driver profile | email={} | Reason={}",
                    driver.getEmail(), ex.getMessage(), ex);
            throw ex;
        }
    }

    // =========================
    // GET ALL DRIVERS
    // =========================
    public List<Driver> getAllDrivers() {

        logger.info("DRIVER-SERVICE: Fetch all drivers request received");

        try {
            List<Driver> drivers = driverRepository.findAll();
            logger.info("DRIVER-SERVICE: Drivers fetched successfully | count={}",
                    drivers != null ? drivers.size() : 0);
            return drivers;

        } catch (Exception ex) {
            logger.error("DRIVER-SERVICE: Failed to fetch drivers | Reason={}",
                    ex.getMessage(), ex);
            throw ex;
        }
    }

    // =========================
    // VIEW ASSIGNED CARGOS FOR A DRIVER
    // =========================
    public List<Cargo> viewDriverCargos(Long driverId) {

        logger.info("DRIVER-SERVICE: View assigned cargos request | driverId={}", driverId);

        if (driverId == null) {
            logger.warn("DRIVER-SERVICE: viewDriverCargos called with null driverId");
            return List.of();
        }

        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

            List<Cargo> cargos = driver.getAssignedCargos();

            logger.info("DRIVER-SERVICE: Assigned cargos fetched | driverId={} | count={}",
                    driverId, cargos != null ? cargos.size() : 0);

            return cargos;

        } catch (EntityNotFoundException ex) {
            logger.warn("DRIVER-SERVICE: Driver not found while fetching cargos | driverId={}", driverId);
            throw ex;

        } catch (Exception ex) {
            logger.error("DRIVER-SERVICE: Failed to fetch assigned cargos | driverId={} | Reason={}",
                    driverId, ex.getMessage(), ex);
            throw ex;
        }
    }

    // =========================
    // UPDATE CARGO STATUS
    // (Pending / In-Transit / Delivered)
    // =========================
    public boolean updateCargoStatus(Long cargoId, String newStatus) {

        logger.info("DRIVER-SERVICE: Update cargo status request | cargoId={} | newStatus={}", cargoId, newStatus);

        if (cargoId == null || newStatus == null || newStatus.trim().isEmpty()) {
            logger.warn("DRIVER-SERVICE: Invalid input for status update | cargoId={} | newStatus={}", cargoId, newStatus);
            return false;
        }

        try {
            Cargo cargo = cargoRepository.findById(cargoId)
                    .orElseThrow(() -> new EntityNotFoundException("Cargo not found."));

            // (Your old check was redundant, but keeping behavior safe)
            if (cargo == null) {
                logger.warn("DRIVER-SERVICE: Cargo is null after fetch (unexpected) | cargoId={}", cargoId);
                return false;
            }

            String oldStatus = cargo.getStatus();
            String normalizedStatus = newStatus.trim().toUpperCase();

            cargo.setStatus(normalizedStatus);
            cargoRepository.save(cargo);

            // ✅ This is the core "Delivered/Transit/Pending" logging you wanted
            logger.info("DRIVER-SERVICE: Cargo status changed | cargoId={} | oldStatus={} | newStatus={}",
                    cargoId, oldStatus, normalizedStatus);

            // Special business event logs (easy to find in console)
            if ("DELIVERED".equalsIgnoreCase(normalizedStatus)) {
                logger.info("✅ DELIVERY EVENT: Cargo delivered | cargoId={}", cargoId);
            } else if ("IN_TRANSIT".equalsIgnoreCase(normalizedStatus)) {
                logger.info("🚚 TRANSIT EVENT: Cargo in transit | cargoId={}", cargoId);
            } else if ("PENDING".equalsIgnoreCase(normalizedStatus)) {
                logger.info("🕒 PENDING EVENT: Cargo pending | cargoId={}", cargoId);
            }

            return true;

        } catch (EntityNotFoundException ex) {
            logger.warn("DRIVER-SERVICE: Status update failed (cargo not found) | cargoId={}", cargoId);
            throw ex;

        } catch (Exception ex) {
            logger.error("DRIVER-SERVICE: Error updating cargo status | cargoId={} | newStatus={} | Reason={}",
                    cargoId, newStatus, ex.getMessage(), ex);
            throw ex;
        }
    }

    // =========================
    // ASSIGN CARGO TO DRIVER (Service-side)
    // =========================
    public boolean assignCargoToDriver(Long cargoId, Long driverId) {

        logger.info("DRIVER-SERVICE: Assign cargo request | cargoId={} | driverId={}", cargoId, driverId);

        try {
            Cargo cargo = cargoRepository.findById(cargoId).orElse(null);
            Driver driver = driverRepository.findById(driverId).orElse(null);

            if (cargo == null || driver == null) {
                logger.warn("DRIVER-SERVICE: Assign cargo failed (cargo/driver missing) | cargoId={} | driverId={}",
                        cargoId, driverId);
                return false;
            }

            Long oldDriverId = (cargo.getDriver() != null) ? cargo.getDriver().getId() : null;

            cargo.setDriver(driver);
            cargoRepository.save(cargo);

            logger.info("DRIVER-SERVICE: Cargo assigned successfully | cargoId={} | oldDriverId={} | newDriverId={}",
                    cargoId, oldDriverId, driverId);

            return true;

        } catch (Exception ex) {
            logger.error("DRIVER-SERVICE: Error assigning cargo | cargoId={} | driverId={} | Reason={}",
                    cargoId, driverId, ex.getMessage(), ex);
            throw ex;
        }
    }
}