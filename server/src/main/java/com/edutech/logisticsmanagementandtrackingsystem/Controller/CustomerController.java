package com.edutech.logisticsmanagementandtrackingsystem.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edutech.logisticsmanagementandtrackingsystem.dto.CargoStatusResponse;
import com.edutech.logisticsmanagementandtrackingsystem.service.CustomerService;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin("*")
public class CustomerController {

@Autowired
private CustomerService customerService;

@GetMapping("/cargo-status")
public ResponseEntity<CargoStatusResponse> viewCargoStatus(@RequestParam Long cargoId) {

CargoStatusResponse response = customerService.viewCargoStatus(cargoId);

if (response != null) {
return ResponseEntity.ok(response);
} else {
return ResponseEntity.notFound().build();
}
}
}