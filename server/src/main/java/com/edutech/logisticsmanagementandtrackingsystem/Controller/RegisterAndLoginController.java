package com.edutech.logisticsmanagementandtrackingsystem.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
@RequestMapping("/api")
public class RegisterAndLoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OtpService otpService;

    // =========================
    // SEND OTP
    // =========================
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String email) {
        otpService.generateAndSendOtp(email);
        return ResponseEntity.ok("OTP sent successfully");
    }

    // =========================
    // REGISTER
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> request) {

        String email = (String) request.get("email");
        String otp = (String) request.get("otp");

        // ✅ OTP validation
        if (!otpService.verifyOtp(email, otp)) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }

        // ✅ Create User
        User user = new User();
        user.setUsername((String) request.get("username"));
        user.setEmail(email);
        user.setPassword((String) request.get("password"));
        user.setRole((String) request.get("role"));

        User savedUser = userService.registerUser(user);
        String role = savedUser.getRole().toUpperCase();

        // ✅ BUSINESS
        if ("BUSINESS".equals(role)) {
            Business business = new Business();
            business.setName(savedUser.getUsername());
            business.setEmail(savedUser.getEmail());
            business.setUser(savedUser);   // ✅ FIX ADDED

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(businessService.createBusiness(business));
        }

        // ✅ CUSTOMER
        if ("CUSTOMER".equals(role)) {
            Customer customer = new Customer();
            customer.setName(savedUser.getUsername());
            customer.setEmail(savedUser.getEmail());
            customer.setUser(savedUser);   // ✅ FIX ADDED

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(customerService.createCustomer(customer));
        }

        // ✅ DRIVER
        if ("DRIVER".equals(role)) {
            Driver driver = new Driver();
            driver.setName(savedUser.getUsername());
            driver.setEmail(savedUser.getEmail());
            driver.setUser(savedUser);     // ✅ FIX ADDED

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(driverService.createDriver(driver));
        }

        return ResponseEntity.badRequest().body("Invalid role");
    }

    // =========================
    // LOGIN
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

        UserDetails userDetails =
                userService.loadUserByUsername(loginRequest.getUsername());

        String token = jwtUtil.generateToken(userDetails.getUsername());
        User user = userService.getUserByUsername(loginRequest.getUsername());

        LoginResponse response = new LoginResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }
}
