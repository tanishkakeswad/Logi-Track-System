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

    //  ADD THIS
    @Autowired private OtpService otpService;

    // =========================
    //  SEND OTP
    // =========================
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String email) {
        otpService.generateAndSendOtp(email);
        return ResponseEntity.ok("OTP sent successfully");
    }

    // =========================
    //  REGISTER WITH OTP
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> request) {

        String email = (String) request.get("email");
        String otp = (String) request.get("otp");

        //  OTP VALIDATION
        String result = otpService.verifyOtp(email, otp);

if (result.equals("SUCCESS")) {
// continue registration
} 
else if (result.equals("OTP_EXPIRED")) {
return ResponseEntity.badRequest().body("OTP expired");
} 
else if (result.equals("MAX_ATTEMPTS_EXCEEDED")) {
return ResponseEntity.badRequest().body("Maximum attempts reached. Please request new OTP");
} 
else if (result.startsWith("INVALID_")) {
int remaining = Integer.parseInt(result.split("_")[1]);
return ResponseEntity.badRequest().body("Invalid OTP. Attempts left: " + remaining);
} 
else {
return ResponseEntity.badRequest().body("OTP not found");
}

        // Convert request → User object
        User user = new User();
        user.setUsername((String) request.get("username"));
        user.setEmail(email);
        user.setPassword((String) request.get("password"));
        user.setRole((String) request.get("role"));

        User savedUser = userService.registerUser(user);
        String role = savedUser.getRole().toUpperCase();

        if (role.equals("BUSINESS")) {
            Business b = new Business();
            b.setName(savedUser.getUsername());
            b.setEmail(savedUser.getEmail());
            b.setUser(savedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(businessService.createBusiness(b));

        } else if (role.equals("CUSTOMER")) {
            Customer c = new Customer();
            c.setName(savedUser.getUsername());
            c.setEmail(savedUser.getEmail());
            c.setUser(savedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(c));

        } else if (role.equals("DRIVER")) {
            Driver d = new Driver();
            d.setName(savedUser.getUsername());
            d.setEmail(savedUser.getEmail());
            d.setUser(savedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(driverService.createDriver(d));
        }

        return ResponseEntity.badRequest().body("Invalid role");
    }

    // =========================
    //  LOGIN (UNCHANGED + YOUR FIX)
    // =========================
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

        //  DRIVER ID FIX (your logic kept)
        if (user.getRole().equals("DRIVER")) {
            Driver driver = driverService.getAllDrivers()
                .stream()
                .filter(d -> d.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);

            if (driver != null) {
                response.setId(driver.getId());
            }
        }

        return ResponseEntity.ok(response);
    }
}

