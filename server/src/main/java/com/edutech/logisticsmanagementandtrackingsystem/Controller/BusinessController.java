package com.edutech.logisticsmanagementandtrackingsystem.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.multipart.MultipartFile;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Driver;
import com.edutech.logisticsmanagementandtrackingsystem.service.CargoService;
import com.edutech.logisticsmanagementandtrackingsystem.service.DriverService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/business")
public class BusinessController {

    private static final Logger logger =
            LoggerFactory.getLogger(BusinessController.class);

    @Autowired
    private CargoService cargoService;

    @Autowired
    private DriverService driverService;

    // =========================
    // ADD CARGO
    // =========================
    @PostMapping("/cargo")
    public ResponseEntity<Cargo> addCargo(@RequestBody Cargo cargo) {

        logger.info("BUSINESS: Add cargo request received");

        try {
            Cargo savedCargo = cargoService.addCargo(cargo);

            logger.info("BUSINESS: Cargo created successfully | cargoId={} | awb={}",
                    savedCargo != null ? savedCargo.getId() : null,
                    savedCargo != null ? savedCargo.getAwb() : null);

            return new ResponseEntity<>(savedCargo, HttpStatus.OK);

        } catch (Exception ex) {
            logger.error("BUSINESS: Failed to add cargo | Reason={}",
                    ex.getMessage(), ex);

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =========================
    // GET ALL DRIVERS
    // =========================
    @GetMapping("/drivers")
    public ResponseEntity<List<Driver>> getAllDrivers() {

        logger.info("BUSINESS: Fetch all drivers request received");

        try {
            List<Driver> drivers = driverService.getAllDrivers();

            logger.info("BUSINESS: Drivers fetched successfully | count={}",
                    drivers != null ? drivers.size() : 0);

            return new ResponseEntity<>(drivers, HttpStatus.OK);

        } catch (Exception ex) {
            logger.error("BUSINESS: Failed to fetch drivers | Reason={}",
                    ex.getMessage(), ex);

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =========================
    // GET ALL CARGO
    // =========================
    @GetMapping("/cargo")
    public ResponseEntity<List<Cargo>> getAllCargo() {

        logger.info("BUSINESS: Fetch all cargo request received");

        try {
            List<Cargo> cargoList = cargoService.viewAllCargo();

            logger.info("BUSINESS: Cargo list fetched successfully | count={}",
                    cargoList != null ? cargoList.size() : 0);

            return new ResponseEntity<>(cargoList, HttpStatus.OK);

        } catch (Exception ex) {
            logger.error("BUSINESS: Failed to fetch cargo list | Reason={}",
                    ex.getMessage(), ex);

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // =========================
    // ASSIGN CARGO TO DRIVER
    // =========================
    @PostMapping("/assign-cargo")
    public ResponseEntity<Map<String, String>> assignCargo(
            @RequestParam Long cargoId,
            @RequestParam Long driverId) {

        logger.info("BUSINESS: Assign cargo request | cargoId={} | driverId={}",
                cargoId, driverId);

        Map<String, String> response = new HashMap<>();

        try {
            boolean assigned = cargoService.assignCargoToDriver(cargoId, driverId);

            if (assigned) {
                response.put("message", "Cargo assigned successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Failed to assign cargo");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception ex) {
            logger.error("BUSINESS: Error while assigning cargo | Reason={}", ex.getMessage(), ex);
            response.put("message", "Failed to assign cargo");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // =========================
    // GET CARGO BY ID
    // =========================
    @GetMapping("/cargo-id")
    public ResponseEntity<Cargo> findCargoById(@RequestParam Long cargoId) {

        logger.info("BUSINESS: Fetch cargo by ID request | cargoId={}", cargoId);

        try {
            Cargo cargo = cargoService.getCargoById(cargoId);

            if (cargo == null) {
                logger.warn("BUSINESS: Cargo not found | cargoId={}", cargoId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok(cargo);

        } catch (Exception ex) {
            logger.error("BUSINESS: Error fetching cargo | Reason={}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =========================
    // ADD CARGO WITH DOCUMENTS (✅ DOCUMENTS MANDATORY)
    // =========================
   @PostMapping(value = "/cargo-with-documents", consumes = "multipart/form-data")
public ResponseEntity<Map<String, String>> addCargoWithDocuments(
        @RequestPart("cargo") Cargo cargo,
        @RequestPart(value = "documents", required = false) MultipartFile[] documents) {

    logger.info("BUSINESS: Add cargo with documents request received");

    Map<String, String> resp = new HashMap<>();

    try {
        // ✅ documents mandatory
        if (documents == null || documents.length == 0) {
            resp.put("message", "Documents are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }

        Cargo savedCargo = cargoService.createCargoWithDocuments(cargo, documents);

        resp.put("message", "Cargo created successfully");
        resp.put("cargoId", savedCargo != null ? String.valueOf(savedCargo.getId()) : null);
        resp.put("awb", savedCargo != null ? savedCargo.getAwb() : null);

        return ResponseEntity.ok(resp);

    } catch (Exception ex) {
        logger.error("BUSINESS: Failed to add cargo with documents | Reason={}", ex.getMessage(), ex);

        // ✅ find the deepest/root cause (actual SQL error)
        Throwable root = ex;
        while (root.getCause() != null) root = root.getCause();

        resp.put("message", "UPLOAD_ERROR");
        resp.put("error", ex.getClass().getSimpleName());
        resp.put("details", ex.getMessage());
        resp.put("rootCause", root.getClass().getSimpleName());
        resp.put("rootMessage", root.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
}
}
