package com.edutech.logisticsmanagementandtrackingsystem.Controller;

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

import com.edutech.logisticsmanagementandtrackingsystem.dto.LoginRequest;
import com.edutech.logisticsmanagementandtrackingsystem.dto.LoginResponse;
import com.edutech.logisticsmanagementandtrackingsystem.entity.*;
import com.edutech.logisticsmanagementandtrackingsystem.jwt.JwtUtil;
import com.edutech.logisticsmanagementandtrackingsystem.service.*;

import java.util.Map;

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

    @Autowired private OtpService otpService;

    // =========================
    //  SEND OTP
    // =========================
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String email) {

        logger.info("SEND-OTP request received for email: {}", email);

        try {
            otpService.generateAndSendOtp(email);
            logger.info("SEND-OTP success for email: {}", email);
            return ResponseEntity.ok("OTP sent successfully");

        } catch (Exception ex) {
            logger.error("SEND-OTP failed for email: {} | Reason: {}", email, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send OTP. Please try again.");
        }
    }

    // =========================
    //  REGISTER WITH OTP
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> request) {

        String username = (String) request.get("username");
        String email = (String) request.get("email");
        String role = (String) request.get("role");
        String otp = (String) request.get("otp");

        logger.info("REGISTER request received | username: {} | email: {} | role: {}", username, email, role);

        try {
            // OTP VALIDATION
            boolean otpValid = otpService.verifyOtp(email, otp);

            if (!otpValid) {
                logger.warn("REGISTER failed (OTP invalid/expired) | email: {} | username: {}", email, username);
                return ResponseEntity.badRequest().body("Invalid or expired OTP");
            }

            // Convert request → User object
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);

            // ✅ DO NOT LOG password
            user.setPassword((String) request.get("password"));
            user.setRole(role);

            User savedUser = userService.registerUser(user);
            String savedRole = savedUser.getRole() != null ? savedUser.getRole().toUpperCase() : "";

            logger.info("REGISTER success | userId: {} | username: {} | role: {}",
                    savedUser.getId(), savedUser.getUsername(), savedRole);

            if (savedRole.equals("BUSINESS")) {
                Business b = new Business();
                b.setName(savedUser.getUsername());
                b.setEmail(savedUser.getEmail());
                b.setUser(savedUser);

                logger.info("Creating BUSINESS profile for userId: {}", savedUser.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(businessService.createBusiness(b));

            } else if (savedRole.equals("CUSTOMER")) {
                Customer c = new Customer();
                c.setName(savedUser.getUsername());
                c.setEmail(savedUser.getEmail());
                c.setUser(savedUser);

                logger.info("Creating CUSTOMER profile for userId: {}", savedUser.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(c));

            } else if (savedRole.equals("DRIVER")) {
                Driver d = new Driver();
                d.setName(savedUser.getUsername());
                d.setEmail(savedUser.getEmail());
                d.setUser(savedUser);

                logger.info("Creating DRIVER profile for userId: {}", savedUser.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(driverService.createDriver(d));
            }

            logger.warn("REGISTER failed (invalid role) | role: {} | username: {}", savedRole, username);
            return ResponseEntity.badRequest().body("Invalid role");

        } catch (Exception ex) {
            logger.error("REGISTER error | username: {} | email: {} | Reason: {}",
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

        logger.info("LOGIN attempt | username: {}", username);

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
        } catch (AuthenticationException e) {

            // ✅ Don't log password, just log username + reason
            logger.warn("LOGIN failed (Invalid credentials) | username: {}", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception ex) {
            logger.error("LOGIN error | username: {} | Reason: {}", username, ex.getMessage(), ex);
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

            logger.info("LOGIN success | username: {} | role: {}", user.getUsername(), user.getRole());

            // DRIVER ID FIX (your logic kept)
            if ("DRIVER".equalsIgnoreCase(user.getRole())) {

                logger.debug("Fetching DRIVER profile for username: {}", username);

                Driver driver = driverService.getAllDrivers()
                        .stream()
                        .filter(d -> d.getUser().getId().equals(user.getId()))
                        .findFirst()
                        .orElse(null);

                if (driver != null) {
                    response.setId(driver.getId());
                    logger.info("Driver profile mapped | username: {} | driverId: {}", username, driver.getId());
                } else {
                    logger.warn("Driver role found but Driver profile missing | username: {}", username);
                }
            }

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            logger.error("LOGIN post-auth error | username: {} | Reason: {}", username, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
