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
         Cargo cargo=cargoRepository.findById(cargoId).get();
        String status=cargo.getStatus();
        return new CargoStatusResponse(cargoId, status);
        
    }

}
