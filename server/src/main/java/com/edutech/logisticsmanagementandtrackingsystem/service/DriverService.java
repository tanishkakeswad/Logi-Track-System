package com.edutech.logisticsmanagementandtrackingsystem.service;

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

 public List<Cargo> viewDriverCargos(Long driverId){
    Driver driver=driverRepository.findById(driverId).get();
    return driver.getAssignedCargos();
 }

 public boolean updateCargoStatus(Long cargoId,String newStatus){
    Cargo cargo=cargoRepository.findById(cargoId).get();
    cargo.setStatus(newStatus);
    return true;
 }

}
