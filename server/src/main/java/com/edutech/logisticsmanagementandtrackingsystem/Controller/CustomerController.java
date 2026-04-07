package com.edutech.logisticsmanagementandtrackingsystem.Controller;

import com.edutech.logisticsmanagementandtrackingsystem.dto.CargoStatusResponse;
import com.edutech.logisticsmanagementandtrackingsystem.service.CustomerService;
import com.edutech.logisticsmanagementandtrackingsystem.service.CargoService;

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

    // ✅ NEW injection (does not break existing)
    @Autowired
    private CargoService cargoService;

    // ✅ Existing endpoint kept exactly
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

    // ✅ NEW endpoint: search by AWB
    @GetMapping("/cargo-status-awb")
    public ResponseEntity<CargoStatusResponse> viewCargoStatusByAwb(@RequestParam String awb) {
        logger.info("Customer requested cargo status by AWB. awb={}", awb);

        try {
            CargoStatusResponse resp = cargoService.getCargoDetailsByAwb(awb);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception ex) {
            logger.warn("Cargo status NOT found by AWB. awb={} | reason={}", awb, ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}