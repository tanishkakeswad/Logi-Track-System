package com.edutech.logisticsmanagementandtrackingsystem.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.edutech.logisticsmanagementandtrackingsystem.dto.LoginRequest;
import com.edutech.logisticsmanagementandtrackingsystem.dto.LoginResponse;
import com.edutech.logisticsmanagementandtrackingsystem.entity.*;
import com.edutech.logisticsmanagementandtrackingsystem.jwt.JwtUtil;
import com.edutech.logisticsmanagementandtrackingsystem.service.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegisterAndLoginController {

    @Autowired private UserService userService;
    @Autowired private BusinessService businessService;
    @Autowired private CustomerService customerService;
    @Autowired private DriverService driverService;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        String role = savedUser.getRole().toUpperCase();

        if (role.equals("BUSINESS")) {
            Business b = new Business();
            b.setName(savedUser.getUsername()); // Fixes $.name path
            b.setEmail(savedUser.getEmail());
            b.setUser(savedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(businessService.createBusiness(b));
        } else if (role.equals("CUSTOMER")) {
            Customer c = new Customer();
            c.setName(savedUser.getUsername()); // Fixes $.name path
            c.setEmail(savedUser.getEmail());
            c.setUser(savedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(c));
        } else if (role.equals("DRIVER")) {
            Driver d = new Driver();
            d.setName(savedUser.getUsername()); // Fixes $.name path
            d.setEmail(savedUser.getEmail());
            d.setUser(savedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(driverService.createDriver(d));
        }
        return ResponseEntity.badRequest().body("Invalid role");
    }

    // @PostMapping("/login")
    // public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
    //     try {
    //         authenticationManager.authenticate(
    //             new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
    //         );
    //     } catch (AuthenticationException e) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    //     }

    //     UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername());
    //     String token = jwtUtil.generateToken(userDetails.getUsername());
    //     User user = userService.getUserByUsername(loginRequest.getUsername());

    //     LoginResponse response = new LoginResponse();
    //     response.setToken(token); // Fixes $.token path
    //     response.setUsername(user.getUsername());
    //     response.setEmail(user.getEmail());
    //     response.setRole(user.getRole());

    //     return ResponseEntity.ok(response);
    // }
    @PostMapping("/login")
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
    try {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );
    } catch (AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername());
    String token = jwtUtil.generateToken(userDetails.getUsername());
    User user = userService.getUserByUsername(loginRequest.getUsername());

    LoginResponse response = new LoginResponse();
    response.setToken(token);
    response.setUsername(user.getUsername());
    response.setEmail(user.getEmail());
    response.setRole(user.getRole());

    // ✅ ADD THIS BLOCK HERE (INSIDE METHOD)
    if (user.getRole().equals("DRIVER")) {
        Driver driver = driverService.getAllDrivers()
            .stream()
            .filter(d -> d.getUser().getId().equals(user.getId()))
            .findFirst()
            .orElse(null);

        if (driver != null) {
            response.setId(driver.getId()); // 🔥 THIS FIXES YOUR ISSUE
        }
    }

    return ResponseEntity.ok(response);
}
}
