package com.edutech.logisticsmanagementandtrackingsystem.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edutech.logisticsmanagementandtrackingsystem.entity.OtpEntity;
import com.edutech.logisticsmanagementandtrackingsystem.repository.OtpRepository;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    // =========================
    // GENERATE & SEND OTP
    // =========================
    public void generateAndSendOtp(String email) {

        logger.info("OTP-SERVICE: Generate OTP request received | email={}", email);

        if (email == null || email.trim().isEmpty()) {
            logger.warn("OTP-SERVICE: OTP generation blocked - email is null/empty");
            return;
        }

        try {
            // ✅ Do NOT log OTP in production
            String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

            Optional<OtpEntity> existing = otpRepository.findByEmail(email);
            OtpEntity entity = existing.orElse(new OtpEntity());

            if (existing.isPresent()) {
                logger.info("OTP-SERVICE: Existing OTP record found, updating | email={}", email);
            } else {
                logger.info("OTP-SERVICE: No OTP record found, creating new | email={}", email);
            }

            entity.setEmail(email);
            entity.setOtp(otp);
            entity.setExpiryTime(LocalDateTime.now().plusMinutes(5));

            // ✅ Reset attempts on new OTP generation
            entity.setAttempts(0);

            otpRepository.save(entity);

            logger.info("OTP-SERVICE: OTP saved successfully (expires in 5 minutes) | email={}", email);

            // Email sending will be logged in EmailService too
            emailService.sendOtp(email, otp);

            logger.info("OTP-SERVICE: OTP send process completed | email={}", email);

        } catch (Exception ex) {
            logger.error("OTP-SERVICE: Failed to generate/send OTP | email={} | Reason={}",
                    email, ex.getMessage(), ex);
            throw ex;
        }
    }

    // =========================
    // VERIFY OTP
    // Returns: OTP_NOT_FOUND / OTP_EXPIRED / MAX_ATTEMPTS_EXCEEDED /
    //          INVALID_<remaining> / SUCCESS
    // =========================
    public String verifyOtp(String email, String otp) {

        logger.info("OTP-SERVICE: Verify OTP request received | email={}", email);

        if (email == null || email.trim().isEmpty()) {
            logger.warn("OTP-SERVICE: OTP verification blocked - email is null/empty");
            return "OTP_NOT_FOUND";
        }

        if (otp == null || otp.trim().isEmpty()) {
            logger.warn("OTP-SERVICE: OTP verification blocked - otp is null/empty | email={}", email);
            return "INVALID_0";
        }

        try {
            Optional<OtpEntity> optional = otpRepository.findByEmail(email);

            if (optional.isEmpty()) {
                logger.warn("OTP-SERVICE: OTP verification failed (no record found) | email={}", email);
                return "OTP_NOT_FOUND";
            }

            OtpEntity entity = optional.get();

            // Check expiry
            if (entity.getExpiryTime() == null || entity.getExpiryTime().isBefore(LocalDateTime.now())) {
                logger.warn("OTP-SERVICE: OTP verification failed (expired OTP) | email={}", email);
                return "OTP_EXPIRED";
            }

            // Check max attempts
            if (entity.getAttempts() >= entity.getMaxAttempts()) {
                logger.warn("OTP-SERVICE: OTP verification blocked (max attempts exceeded) | email={} | attempts={}",
                        email, entity.getAttempts());
                return "MAX_ATTEMPTS_EXCEEDED";
            }

            // Wrong OTP
            if (entity.getOtp() == null || !entity.getOtp().equals(otp)) {

                entity.setAttempts(entity.getAttempts() + 1);
                otpRepository.save(entity);

                int remaining = entity.getMaxAttempts() - entity.getAttempts();

                logger.warn("OTP-SERVICE: OTP verification failed (wrong OTP) | email={} | attempts={} | remaining={}",
                        email, entity.getAttempts(), remaining);

                return "INVALID_" + remaining;
            }

            // Correct OTP
            otpRepository.delete(entity);

            logger.info("OTP-SERVICE: OTP verification SUCCESS | email={}", email);

            return "SUCCESS";

        } catch (Exception ex) {
            logger.error("OTP-SERVICE: Error during OTP verification | email={} | Reason={}",
                    email, ex.getMessage(), ex);
            throw ex;
        }
    }
}
