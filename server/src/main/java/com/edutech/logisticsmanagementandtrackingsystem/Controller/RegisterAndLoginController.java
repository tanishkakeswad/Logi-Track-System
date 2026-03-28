package com.edutech.logisticsmanagementandtrackingsystem.Controller;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
import com.edutech.logisticsmanagementandtrackingsystem.service.UserService;

@RestController
@RequestMapping
public class RegisterAndLoginController {
        // register user in user repository by user service
        // after register in user repository then based on provided user role, register user in business, customer or driver repository
        // return with registered user 200 OK
        // implement login logic here
        // return valid jwt token in loginResponse
        // return 401 unauthorized if login failed
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

    /**
     * Register user based on role
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {

        // Save user in User table
        User savedUser = userService.registerUser(user);

        // Role-based registration
        switch (savedUser.getRole().toUpperCase()) {

            case "BUSINESS":
                Business business = new Business();
                business.setUser(savedUser);
                businessService.createBusiness(business);
                break;

            case "CUSTOMER":
                Customer customer = new Customer();
                customer.setUser(savedUser);
                customerService.createCustomer(customer);
                break;

            case "DRIVER":
                Driver driver = new Driver();
                driver.setUser(savedUser);
                driverService.createDriver(driver);
                break;

            default:
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid role specified"
                );
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(savedUser);
    }

    /**
     * Login user and generate JWT token
     */
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
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password"
            );
        }

        // Load authenticated user details
        UserDetails userDetails = userService
                .loadUserByUsername(loginRequest.getUsername());

        // Generate JWT token
        String token = jwtUtil.generateToken(userDetails);

        // Fetch user entity for response data
        User user = userService
                .getUserByUsername(loginRequest.getUsername());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        return ResponseEntity.ok(response);
    }
}