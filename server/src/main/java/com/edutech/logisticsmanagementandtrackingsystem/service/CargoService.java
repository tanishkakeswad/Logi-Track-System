package com.edutech.logisticsmanagementandtrackingsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Driver;
import com.edutech.logisticsmanagementandtrackingsystem.repository.CargoRepository;
import com.edutech.logisticsmanagementandtrackingsystem.repository.DriverRepository;

import javax.persistence.EntityNotFoundException;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class CargoService {
    // implement service logic here
    @Autowired
    CargoRepository cargoRepository;
    @Autowired
    DriverRepository driverRepository;

    public Cargo addCargo(Cargo cargo) {
        return cargoRepository.save(cargo);
    }

    public List<Cargo> viewAllCargo() {
        return cargoRepository.findAll();
    }



    public boolean assignCargoToDriver(long cargoId, Long driverId) {
        Cargo cargo = cargoRepository.findById(cargoId)
                .orElseThrow(() -> new EntityNotFoundException("Cargo not found"));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        cargo.setDriver(driver);
        cargoRepository.save(cargo); 
        return true;
    }

    public boolean updateCargoStatus(Long cargoId, String newStatus) {

    Cargo cargo = cargoRepository.findById(cargoId)
    .orElseThrow(() -> new EntityNotFoundException("Cargo not found"));

    cargo.setStatus(newStatus.toUpperCase()); 
    cargoRepository.save(cargo);

    return true;
    }
    public Cargo getCargoById(long cargoId) {
        return cargoRepository.findById(cargoId).orElse(null);
    }

}
