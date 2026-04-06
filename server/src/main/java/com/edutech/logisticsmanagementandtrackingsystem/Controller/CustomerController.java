 package com.edutech.logisticsmanagementandtrackingsystem.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edutech.logisticsmanagementandtrackingsystem.dto.CargoStatusResponse;
import com.edutech.logisticsmanagementandtrackingsystem.service.CustomerService;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private static final Logger logger =
            LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    // =========================
    // GET CARGO STATUS
    // =========================
    @GetMapping("/cargo-status")
    public ResponseEntity<CargoStatusResponse> viewCargoStatus(@RequestParam Long cargoId) {

        logger.info("CUSTOMER: Cargo status request received | cargoId={}", cargoId);

        try {
            CargoStatusResponse cargoStatusResponse = customerService.viewCargoStatus(cargoId);

            if (cargoStatusResponse != null) {
                logger.info("CUSTOMER: Cargo status found | cargoId={}", cargoId);
                return new ResponseEntity<>(cargoStatusResponse, HttpStatus.OK);
            } else {
                logger.warn("CUSTOMER: Cargo status NOT found | cargoId={}", cargoId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (Exception ex) {
            logger.error("CUSTOMER: Error while fetching cargo status | cargoId={} | Reason={}",
                    cargoId, ex.getMessage(), ex);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
