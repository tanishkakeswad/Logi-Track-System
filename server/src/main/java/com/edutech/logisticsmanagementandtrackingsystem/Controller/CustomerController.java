package com.edutech.logisticsmanagementandtrackingsystem.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edutech.logisticsmanagementandtrackingsystem.dto.CargoStatusResponse;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;
import com.edutech.logisticsmanagementandtrackingsystem.service.CargoService;
import com.edutech.logisticsmanagementandtrackingsystem.service.CustomerService;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;


        // get cargo status and return it with status code 200
        // public ResponseEntity<CargoStatusResponse> viewCargoStatus(@RequestParam Long cargoId){
        //     CargoStatusResponse cargoStatusResponse = customerService.
        // }
        // if cargo status is not found, return 404 status code
    }


