package com.edutech.logisticsmanagementandtrackingsystem.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import com.edutech.logisticsmanagementandtrackingsystem.repository.DriverLocationRepository;
import com.edutech.logisticsmanagementandtrackingsystem.entity.DriverLocation;
import com.edutech.logisticsmanagementandtrackingsystem.dto.LocationDto;

@RestController
@RequestMapping("/api/location")
@CrossOrigin(origins = "*")
public class LocationController {

    private static final Logger logger =
            LoggerFactory.getLogger(LocationController.class);

    @Autowired
    private DriverLocationRepository repo;

    // =========================
    // UPDATE DRIVER LOCATION
    // =========================
    @PostMapping("/update")
    public void updateLocation(@RequestBody LocationDto dto) {

        if (dto == null || dto.getDriverId() == null) {
            logger.warn("LOCATION: Update request received with missing driverId");
            return;
        }

        Long driverId = dto.getDriverId();

        logger.info("LOCATION: Update request received | driverId={} | lat={} | lng={}",
                driverId, dto.getLat(), dto.getLng());

        try {
            DriverLocation loc = repo.findById(driverId)
                    .orElseGet(() -> {
                        logger.info("LOCATION: No existing location found, creating new record | driverId={}", driverId);
                        return new DriverLocation();
                    });

            loc.setDriverId(driverId);
            loc.setLat(dto.getLat());
            loc.setLng(dto.getLng());

            repo.save(loc);

            logger.info("LOCATION: Location updated successfully | driverId={}", driverId);

        } catch (Exception ex) {
            logger.error("LOCATION: Failed to update location | driverId={} | Reason={}",
                    driverId, ex.getMessage(), ex);
        }
    }

    // =========================
    // GET LOCATION BY DRIVER ID
    // =========================
    @GetMapping("/{driverId}")
    public DriverLocation getLocation(@PathVariable Long driverId) {

        logger.info("LOCATION: Get location request received | driverId={}", driverId);

        try {
            DriverLocation loc = repo.findById(driverId).orElse(null);

            if (loc == null) {
                logger.warn("LOCATION: Location not found | driverId={}", driverId);
            } else {
                logger.info("LOCATION: Location found | driverId={} | lat={} | lng={}",
                        driverId, loc.getLat(), loc.getLng());
            }

            return loc;

        } catch (Exception ex) {
            logger.error("LOCATION: Failed to fetch location | driverId={} | Reason={}",
                    driverId, ex.getMessage(), ex);
            return null;
        }
    }

    // =========================
    // GET ALL LOCATIONS
    // =========================
    @GetMapping("/all")
    public List<DriverLocation> getAllLocations() {

        logger.info("LOCATION: Get all locations request received");

        try {
            List<DriverLocation> list = repo.findAll();
            logger.info("LOCATION: Locations fetched successfully | count={}",
                    list != null ? list.size() : 0);
            return list;

        } catch (Exception ex) {
            logger.error("LOCATION: Failed to fetch all locations | Reason={}",
                    ex.getMessage(), ex);
            return List.of();
        }
    }
}