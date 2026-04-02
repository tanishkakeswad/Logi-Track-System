package com.edutech.logisticsmanagementandtrackingsystem.Controller;

import org.springframework.web.multipart.MultipartFile;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Driver;
import com.edutech.logisticsmanagementandtrackingsystem.service.CargoService;
import com.edutech.logisticsmanagementandtrackingsystem.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/business")
public class BusinessController {
    
    @Autowired
    private CargoService cargoService;

    @Autowired
    private DriverService driverService;



    @PostMapping("/cargo")
    public ResponseEntity<Cargo> addCargo( @RequestBody Cargo cargo ){
        return new  ResponseEntity<Cargo>(cargoService.addCargo(cargo),HttpStatus.OK);
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<Driver>> getAllDrivers(){
        return new ResponseEntity<List<Driver>>(driverService.getAllDrivers(),HttpStatus.OK);
    }


    @GetMapping("/cargo")
    public ResponseEntity<List<Cargo>> getAllCargo(){
        return new ResponseEntity<>(cargoService.viewAllCargo(),HttpStatus.OK);
    }

    @PostMapping("/assign-cargo")
    public ResponseEntity<Map<String,String>> assignCargo(@RequestParam Long cargoId ,@RequestParam Long driverId){
        Map<String,String> response = new HashMap<>();

        try {
            boolean assigned = cargoService.assignCargoToDriver(cargoId, driverId);

            if (assigned) {
                response.put("message", "Cargo assigned successfully");
                return ResponseEntity.ok(response);
            }else{
                 response.put("message", "Failed to assign cargo");

            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
              response.put("message", "Failed to assign cargo");

            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
      
    }
    
    @GetMapping("/cargo-id")
public ResponseEntity<Cargo> findCargoById(@RequestParam Long cargoId) {
    Cargo cargo = cargoService.getCargoById(cargoId);

    if (cargo == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    return ResponseEntity.ok(cargo);
}
    @PostMapping("/cargo-with-documents")
public ResponseEntity<Cargo> addCargoWithDocuments(
        @RequestPart("cargo") Cargo cargo,
        @RequestPart(value = "documents", required = false) MultipartFile[] documents) {

    Cargo savedCargo = cargoService.createCargoWithDocuments(cargo, documents);
    return ResponseEntity.ok(savedCargo);
}
    
    
}
