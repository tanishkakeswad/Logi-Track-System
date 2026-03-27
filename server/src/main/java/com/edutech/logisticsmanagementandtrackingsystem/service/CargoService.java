package com.edutech.logisticsmanagementandtrackingsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Driver;
import com.edutech.logisticsmanagementandtrackingsystem.repository.CargoRepository;
import com.edutech.logisticsmanagementandtrackingsystem.repository.DriverRepository;

import javax.persistence.EntityNotFoundException;

import java.util.Collections;
import java.util.List;


@Service
public class CargoService {
 // implement service logic here
 @Autowired
 CargoRepository cargoRepository;
 @Autowired
 DriverRepository driverRepository;

 

public Cargo addCargo(Cargo cargo){
    return cargoRepository.save(cargo);
}

public List<Cargo> viewAllCargo(){
    return cargoRepository.findAll();
}

public boolean assignCargoToDriver(long cargoId,Long driverId){
    Cargo carg=cargoRepository.findById(cargoId).get();
    Driver driver=driverRepository.findById(driverId).get();
    driver.setAssignedCargos(Collections.singletonList(carg));
    return true;
}

}
