package com.edutech.logisticsmanagementandtrackingsystem.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edutech.logisticsmanagementandtrackingsystem.entity.OtpEntity;
import com.edutech.logisticsmanagementandtrackingsystem.repository.OtpRepository;

@Service
public class OtpService {

@Autowired
private OtpRepository otpRepository;

@Autowired
private EmailService emailService;

// =========================
// GENERATE OTP
// =========================
public void generateAndSendOtp(String email) {
String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

OtpEntity entity = otpRepository.findByEmail(email)
.orElse(new OtpEntity());

entity.setEmail(email);
entity.setOtp(otp);
entity.setExpiryTime(LocalDateTime.now().plusMinutes(5));
entity.setAttempts(0); // RESET attempts

otpRepository.save(entity);

emailService.sendOtp(email, otp);
}

// =========================
// VERIFY OTP
// =========================
public String verifyOtp(String email, String otp) {
Optional<OtpEntity> optional = otpRepository.findByEmail(email);

if (optional.isEmpty()) {
return "OTP_NOT_FOUND";
}

OtpEntity entity = optional.get();

// Check expiry
if (entity.getExpiryTime().isBefore(LocalDateTime.now())) {
return "OTP_EXPIRED";
}

// Check max attempts
if (entity.getAttempts() >= entity.getMaxAttempts()) {
return "MAX_ATTEMPTS_EXCEEDED";
}

// Wrong OTP
if (!entity.getOtp().equals(otp)) {
entity.setAttempts(entity.getAttempts() + 1);
otpRepository.save(entity);

int remaining = entity.getMaxAttempts() - entity.getAttempts();
return "INVALID_" + remaining;
}

// Correct OTP
otpRepository.delete(entity);
return "SUCCESS";
}
}