package com.edutech.logisticsmanagementandtrackingsystem.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edutech.logisticsmanagementandtrackingsystem.dto.CargoStatusResponse;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Customer;
import com.edutech.logisticsmanagementandtrackingsystem.repository.CargoRepository;
import com.edutech.logisticsmanagementandtrackingsystem.repository.CustomerRepository;

@Service
public class CustomerService {
 // implement service logic here
    @Autowired
    private CargoRepository cargoRepository;
    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer){
        return customerRepository.save(customer);
    }

    public CargoStatusResponse viewCargoStatus(Long cargoId){

    Cargo cargo = cargoRepository.findById(cargoId).orElse(null);

    if (cargo == null) {
        return null; // controller will return 404
    }

    CargoStatusResponse response = new CargoStatusResponse();
    response.setCargoId(cargo.getId());   // 🔥 IMPORTANT
    response.setStatus(cargo.getStatus());

    return response;
}
}
