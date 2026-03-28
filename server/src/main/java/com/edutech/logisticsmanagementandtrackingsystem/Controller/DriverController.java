package com.edutech.logisticsmanagementandtrackingsystem.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;
import com.edutech.logisticsmanagementandtrackingsystem.service.DriverService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/driver")
public class DriverController {


   @Autowired
   private DriverService driverService;

   @GetMapping("/cargo")
   public ResponseEntity<List<Cargo>> assignedCargo(@RequestParam Long driverId){
      return new ResponseEntity<List<Cargo>>(driverService.viewDriverCargos(driverId),HttpStatus.OK);
   }

   @PutMapping("/update-cargo-status")
   public ResponseEntity<Map<String,String>> updateCargoStatus(@RequestParam Long cargoId, @RequestParam String newStatus ){
      Map<String,String> response  = new HashMap<>();

      try {
         boolean updated = driverService.updateCargoStatus(cargoId, newStatus);
         if (updated) {
            response.put("message", "Cargo status updated successfully");
            return ResponseEntity.ok(response);
            
         }else{
            response.put("message","Failed to update cargo status");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
         }
      } catch (Exception e) {
          response.put("message","Failed to update cargo status");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        
      }
   }


   


}
