package com.edutech.logisticsmanagementandtrackingsystem.Controller;

import com.edutech.logisticsmanagementandtrackingsystem.dto.LoginRequest;
import com.edutech.logisticsmanagementandtrackingsystem.dto.LoginResponse;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Business;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Customer;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Driver;
import com.edutech.logisticsmanagementandtrackingsystem.entity.User;
import com.edutech.logisticsmanagementandtrackingsystem.jwt.JwtUtil;
import com.edutech.logisticsmanagementandtrackingsystem.service.BusinessService;
import com.edutech.logisticsmanagementandtrackingsystem.service.CustomerService;
import com.edutech.logisticsmanagementandtrackingsystem.service.DriverService;
import com.edutech.logisticsmanagementandtrackingsystem.service.OtpService;
import com.edutech.logisticsmanagementandtrackingsystem.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class RegisterAndLoginController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterAndLoginController.class);

    @Autowired private UserService userService;
    @Autowired private BusinessService businessService;
    @Autowired private CustomerService customerService;
    @Autowired private DriverService driverService;

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;

    // OTP service
    @Autowired private OtpService otpService;

    // =========================
    //  SEND OTP
    // =========================
    @PostMapping("/send-otp")
public ResponseEntity<?> sendOtp(@RequestParam String email) {
    try {
        otpService.generateAndSendOtp(email);
        
        // Return a Map (which Jackson converts to JSON: {"message": "..."})
        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP sent successfully");
        
        return ResponseEntity.ok(response); 
    } catch (Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Failed to send OTP");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

    // =========================
    //  REGISTER WITH OTP
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> request) {

        String username = (String) request.get("username");
        String email    = (String) request.get("email");
        String role     = (String) request.get("role");
        String otp      = (String) request.get("otp");

        logger.info("REGISTER request received | username={} | email={} | role={}", username, email, role);

        try {
            // =========================
            // OTP VALIDATION (String result logic kept)
            // =========================
            String result = otpService.verifyOtp(email, otp);

            if ("SUCCESS".equals(result)) {
                logger.info("REGISTER OTP verified | email={}", email);
                // continue registration
            } else if ("OTP_EXPIRED".equals(result)) {
                logger.warn("REGISTER failed | OTP expired | email={}", email);
                return ResponseEntity.badRequest().body("OTP expired");
            } else if ("MAX_ATTEMPTS_EXCEEDED".equals(result)) {
                logger.warn("REGISTER failed | max attempts exceeded | email={}", email);
                return ResponseEntity.badRequest().body("Maximum attempts reached. Please request new OTP");
            } else if (result != null && result.startsWith("INVALID_")) {
                int remaining = Integer.parseInt(result.split("_")[1]);
                logger.warn("REGISTER failed | invalid OTP | email={} | attemptsLeft={}", email, remaining);
                return ResponseEntity.badRequest().body("Invalid OTP. Attempts left: " + remaining);
            } else {
                logger.warn("REGISTER failed | OTP not found/invalid state | email={} | result={}", email, result);
                return ResponseEntity.badRequest().body("OTP not found");
            }

            // =========================
            // Convert request → User object
            // =========================
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);

            // ✅ NEVER log password
            user.setPassword((String) request.get("password"));
            user.setRole(role);

            User savedUser = userService.registerUser(user);
            String savedRole = (savedUser.getRole() != null) ? savedUser.getRole().toUpperCase() : "";

            logger.info("REGISTER success | userId={} | username={} | role={}",
                    savedUser.getId(), savedUser.getUsername(), savedRole);

            // =========================
            // Create corresponding profile by role
            // =========================
            if ("BUSINESS".equals(savedRole)) {
                Business b = new Business();
                b.setName(savedUser.getUsername());
                b.setEmail(savedUser.getEmail());
                b.setUser(savedUser);

                logger.info("Creating BUSINESS profile | userId={}", savedUser.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(businessService.createBusiness(b));

            } else if ("CUSTOMER".equals(savedRole)) {
                Customer c = new Customer();
                c.setName(savedUser.getUsername());
                c.setEmail(savedUser.getEmail());
                c.setUser(savedUser);

                logger.info("Creating CUSTOMER profile | userId={}", savedUser.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(c));

            } else if ("DRIVER".equals(savedRole)) {
                Driver d = new Driver();
                d.setName(savedUser.getUsername());
                d.setEmail(savedUser.getEmail());
                d.setUser(savedUser);

                logger.info("Creating DRIVER profile | userId={}", savedUser.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(driverService.createDriver(d));
            }

            logger.warn("REGISTER failed | invalid role | role={} | username={}", savedRole, username);
            return ResponseEntity.badRequest().body("Invalid role");

        } catch (Exception ex) {
            logger.error("REGISTER error | username={} | email={} | reason={}",
                    username, email, ex.getMessage(), ex);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed. Please try again.");
        }
    }

    // =========================
    //  LOGIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        String username = loginRequest.getUsername();
        logger.info("LOGIN attempt | username={}", username);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            logger.warn("LOGIN failed | invalid credentials | username={}", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception ex) {
            logger.error("LOGIN error | username={} | reason={}", username, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        try {
            UserDetails userDetails = userService.loadUserByUsername(username);

            // ✅ Do not log token
            String token = jwtUtil.generateToken(userDetails.getUsername());

            User user = userService.getUserByUsername(username);

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());

            logger.info("LOGIN success | username={} | role={}", user.getUsername(), user.getRole());

            // DRIVER ID FIX (your logic kept)
            if ("DRIVER".equalsIgnoreCase(user.getRole())) {
                logger.debug("Fetching DRIVER profile | username={}", username);

                Driver driver = driverService.getAllDrivers()
                        .stream()
                        .filter(d -> d.getUser() != null
                                && d.getUser().getId() != null
                                && d.getUser().getId().equals(user.getId()))
                        .findFirst()
                        .orElse(null);

                if (driver != null) {
                    response.setId(driver.getId());
                    logger.info("Driver mapped | username={} | driverId={}", username, driver.getId());
                } else {
                    logger.warn("Driver role but Driver profile missing | username={}", username);
                }
            }

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            logger.error("LOGIN post-auth error | username={} | reason={}", username, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
