package com.edutech.logisticsmanagementandtrackingsystem.Controller;

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

@Autowired
private DriverLocationRepository repo;

@PostMapping("/update")
public void updateLocation(@RequestBody LocationDto dto) {

DriverLocation loc = repo.findById(dto.getDriverId())
.orElse(new DriverLocation());

loc.setDriverId(dto.getDriverId());
loc.setLat(dto.getLat());
loc.setLng(dto.getLng());

repo.save(loc);
}

@GetMapping("/{driverId}")
public DriverLocation getLocation(@PathVariable Long driverId) {
System.out.println("API HIT: " + driverId); // debug
return repo.findById(driverId).orElse(null);
}

@GetMapping("/all")
public List<DriverLocation> getAllLocations() {
return repo.findAll();
}
}
