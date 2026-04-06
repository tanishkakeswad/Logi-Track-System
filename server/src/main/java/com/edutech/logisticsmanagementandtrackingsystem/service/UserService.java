package com.edutech.logisticsmanagementandtrackingsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.edutech.logisticsmanagementandtrackingsystem.entity.User;
import com.edutech.logisticsmanagementandtrackingsystem.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =========================
    // REGISTER USER
    // =========================
    public User registerUser(User user) {

        if (user == null) {
            logger.warn("USER-SERVICE: registerUser called with null user");
            return null;
        }

        // ✅ Never log password
        logger.info("USER-SERVICE: Registration request | username={} | email={} | role={}",
                user.getUsername(), user.getEmail(), user.getRole());

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User saved = userRepository.save(user);

            logger.info("USER-SERVICE: User registered successfully | userId={} | username={}",
                    saved != null ? saved.getId() : null,
                    saved != null ? saved.getUsername() : null
            );

            return saved;

        } catch (Exception ex) {
            logger.error("USER-SERVICE: Registration failed | username={} | Reason={}",
                    user.getUsername(), ex.getMessage(), ex);
            throw ex;
        }
    }

    // =========================
    // GET USER BY USERNAME
    // =========================
    public User getUserByUsername(String username) {

        logger.info("USER-SERVICE: Fetch user by username request | username={}", username);

        if (username == null || username.trim().isEmpty()) {
            logger.warn("USER-SERVICE: getUserByUsername called with null/empty username");
            return null;
        }

        try {
            User user = userRepository.findByUsername(username);

            if (user == null) {
                logger.warn("USER-SERVICE: User not found | username={}", username);
            } else {
                logger.info("USER-SERVICE: User found | username={} | role={}", user.getUsername(), user.getRole());
            }

            return user;

        } catch (Exception ex) {
            logger.error("USER-SERVICE: Error fetching user | username={} | Reason={}",
                    username, ex.getMessage(), ex);
            throw ex;
        }
    }

    // =========================
    // SPRING SECURITY: LOAD USER
    // =========================
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        logger.info("USER-SERVICE: loadUserByUsername called | username={}", username);

        if (username == null || username.trim().isEmpty()) {
            logger.warn("USER-SERVICE: loadUserByUsername blocked - username null/empty");
            throw new UsernameNotFoundException("Username is empty");
        }

        try {
            User user = userRepository.findByUsername(username);

            if (user == null) {
                logger.warn("USER-SERVICE: UserDetails load failed - user not found | username={}", username);
                throw new UsernameNotFoundException("User not found with username: " + username);
            }

            logger.info("USER-SERVICE: UserDetails loaded successfully | username={} | role={}",
                    user.getUsername(), user.getRole());

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole()) // BUSINESS / DRIVER / CUSTOMER
                    .build();

        } catch (UsernameNotFoundException ex) {
            throw ex; // keep standard behavior

        } catch (Exception ex) {
            logger.error("USER-SERVICE: Error in loadUserByUsername | username={} | Reason={}",
                    username, ex.getMessage(), ex);
            throw ex;
        }
    }
}