package com.edutech.logisticsmanagementandtrackingsystem.Controller;

import com.edutech.logisticsmanagementandtrackingsystem.dto.CargoStatusResponse;
import com.edutech.logisticsmanagementandtrackingsystem.service.CustomerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin("*")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    // get cargo status and return it with status code 200
    @GetMapping("/cargo-status")
    public ResponseEntity<CargoStatusResponse> viewCargoStatus(@RequestParam Long cargoId) {
        logger.info("Customer requested cargo status. cargoId={}", cargoId);

        CargoStatusResponse cargoStatusResponse = customerService.viewCargoStatus(cargoId);

        if (cargoStatusResponse != null) {
            logger.info("Cargo status found. cargoId={}", cargoId);
            return new ResponseEntity<>(cargoStatusResponse, HttpStatus.OK);
        } else {
            logger.warn("Cargo status NOT found. cargoId={}", cargoId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    // if cargo status is not found, return 404 status code
}