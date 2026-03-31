package com.edutech.logisticsmanagementandtrackingsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Driver;
import com.edutech.logisticsmanagementandtrackingsystem.repository.CargoRepository;
import com.edutech.logisticsmanagementandtrackingsystem.repository.DriverRepository;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class DriverService {
 // implement service logic here
 @Autowired
 private DriverRepository driverRepository;
 @Autowired
 private CargoRepository cargoRepository;

 public Driver createDriver(Driver driver){
    return driverRepository.save(driver);
 }

 public List<Driver> getAllDrivers(){
    return driverRepository.findAll();
 }

public List<Cargo> viewDriverCargos(Long driverId) {

    return cargoRepository.findAll()
        .stream()
        .filter(c -> c.getDriver() != null &&
                     c.getDriver().getId().equals(driverId))
        .collect(Collectors.toList());
}



public boolean updateCargoStatus(Long cargoId, String newStatus) {

    Cargo cargo = cargoRepository.findById(cargoId)
        .orElseThrow(()->new EntityNotFoundException("Cargo not found."));

    if (cargo == null) return false;

    cargo.setStatus(newStatus.toUpperCase());
    cargoRepository.save(cargo);

    return true;
}
public boolean assignCargoToDriver(Long cargoId, Long driverId) {

    Cargo cargo = cargoRepository.findById(cargoId).orElse(null);
    Driver driver = driverRepository.findById(driverId).orElse(null);

    if (cargo == null || driver == null) return false;

    cargo.setDriver(driver);
    cargoRepository.save(cargo);

    return true;
}
}
