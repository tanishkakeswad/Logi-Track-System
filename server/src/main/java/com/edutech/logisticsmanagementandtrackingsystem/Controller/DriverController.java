package com.edutech.logisticsmanagementandtrackingsystem.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;
import com.edutech.logisticsmanagementandtrackingsystem.service.DriverService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

   private static final Logger logger =
           LoggerFactory.getLogger(DriverController.class);

   @Autowired
   private DriverService driverService;

   // =========================
   // GET ASSIGNED CARGO FOR DRIVER
   // =========================
   @GetMapping("/cargo")
   public ResponseEntity<List<Cargo>> assignedCargo(@RequestParam Long driverId) {

      logger.info("DRIVER: Fetch assigned cargo request received | driverId={}", driverId);

      try {
         List<Cargo> cargos = driverService.viewDriverCargos(driverId);

         logger.info("DRIVER: Assigned cargo fetched successfully | driverId={} | count={}",
                 driverId, cargos != null ? cargos.size() : 0);

         return new ResponseEntity<>(cargos, HttpStatus.OK);

      } catch (Exception ex) {
         logger.error("DRIVER: Failed to fetch assigned cargo | driverId={} | Reason={}",
                 driverId, ex.getMessage(), ex);

         return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   // =========================
   // UPDATE CARGO STATUS
   // =========================
   @PutMapping("/update-cargo-status")
   public ResponseEntity<Map<String, String>> updateCargoStatus(
           @RequestParam Long cargoId,
           @RequestParam String newStatus) {

      logger.info("DRIVER: Update cargo status request | cargoId={} | newStatus={}", cargoId, newStatus);

      Map<String, String> response = new HashMap<>();

      try {
         boolean updated = driverService.updateCargoStatus(cargoId, newStatus);

         if (updated) {
            logger.info("DRIVER: Cargo status updated successfully | cargoId={} | newStatus={}", cargoId, newStatus);
            response.put("message", "Cargo status updated successfully");
            return ResponseEntity.ok(response);

         } else {
            logger.warn("DRIVER: Cargo status update failed (not found/invalid) | cargoId={} | newStatus={}",
                    cargoId, newStatus);

            response.put("message", "Failed to update cargo status");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
         }

      } catch (Exception ex) {
         logger.error("DRIVER: Error updating cargo status | cargoId={} | newStatus={} | Reason={}",
                 cargoId, newStatus, ex.getMessage(), ex);

         response.put("message", "Failed to update cargo status");
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }
   }
}