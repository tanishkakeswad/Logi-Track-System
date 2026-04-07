package com.edutech.logisticsmanagementandtrackingsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String toEmail, String otp) {

        // ✅ Never log OTP in production
        logger.info("EMAIL-SERVICE: OTP email send request | toEmail={}", toEmail);

        if (toEmail == null || toEmail.trim().isEmpty()) {
            logger.warn("EMAIL-SERVICE: OTP email blocked - toEmail is null/empty");
            return;
        }

        if (otp == null || otp.trim().isEmpty()) {
            logger.warn("EMAIL-SERVICE: OTP email blocked - otp is null/empty | toEmail={}", toEmail);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("OTP Verification for CARGOWALA");

            String htmlContent =
                    "<!DOCTYPE html>" +
                    "<html>" +
                    "<body style='margin:0; padding:0; background:#f4f6f9; font-family:Arial, sans-serif;'>" +

                    "<div style='max-width:420px; margin:40px auto; background:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 6px 16px rgba(0,0,0,0.1);'>" +

                    "<div style='background:#4f46e5; padding:20px; text-align:center; color:white;'>" +
                    "<h2 style='margin:0;'>CARGOWALA</h2>" +
                    "<p style='margin:5px 0 0; font-size:13px;'>Secure OTP Verification</p>" +
                    "</div>" +

                    "<div style='padding:30px; text-align:center;'>" +
                    "<h3>Your One-Time Password</h3>" +
                    "<p style='color:#666; font-size:14px;'>Use the OTP below to complete your verification.</p>" +

                    "<div style='margin:25px 0; font-size:30px; font-weight:bold; letter-spacing:8px; color:#4f46e5; border:2px dashed #4F46E5; padding:12px 20px; display:inline-block; border-radius:10px;'>" +
                    otp +
                    "</div>" +

                    "<p style='font-size:13px; color:#999;'>Do not share this code.</p>" +
                    "</div>" +

                    "<div style='text-align:center; padding:15px; font-size:12px; color:#aaa; border-top:1px solid #eee;'>" +
                    "If you didn't request this, ignore this email.<br><br>" +
                    "© 2026 CARGOWALA" +
                    "</div>" +

                    "</div>" +

                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);

            mailSender.send(message);

            logger.info("EMAIL-SERVICE: OTP email sent successfully | toEmail={}", toEmail);

        } catch (Exception ex) {
            logger.error("EMAIL-SERVICE: Failed to send OTP email | toEmail={} | Reason={}",
                    toEmail, ex.getMessage(), ex);

            // ✅ Throw so controller can return proper error response
            throw new RuntimeException("Failed to send OTP email", ex);
        }
    }
}