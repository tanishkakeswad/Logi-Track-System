package com.edutech.logisticsmanagementandtrackingsystem.service;

import com.edutech.logisticsmanagementandtrackingsystem.dto.PaymentOrderResponse;
import com.edutech.logisticsmanagementandtrackingsystem.dto.PaymentRequest;
import com.edutech.logisticsmanagementandtrackingsystem.dto.VerifyPaymentRequest;
import com.edutech.logisticsmanagementandtrackingsystem.entity.Payment;
import com.edutech.logisticsmanagementandtrackingsystem.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    private final PricingService pricingService;
    private final PaymentRepository paymentRepository;
    private final RazorpayClientService razorpayClientService;

    public PaymentService(PricingService pricingService,
                          PaymentRepository paymentRepository,
                          RazorpayClientService razorpayClientService) {
        this.pricingService = pricingService;
        this.paymentRepository = paymentRepository;
        this.razorpayClientService = razorpayClientService;
    }

    public PaymentOrderResponse createOrder(PaymentRequest request) {

        // ✅ Amount calculated ONLY in backend (size = weight in KG)
        double amountRupees = pricingService.calculateAmount(
                request.getSourceCity(),
                request.getDestinationCity(),
                request.getSize()
        );

        long amountPaise = Math.round(amountRupees * 100);

        Map<String, Object> order = razorpayClientService.createOrder(
                amountPaise,
                "rcpt_" + System.currentTimeMillis()
        );

        String orderId = (String) order.get("id");

        Payment payment = new Payment();
        payment.setSourceCity(request.getSourceCity());
        payment.setDestinationCity(request.getDestinationCity());
        payment.setSize(request.getSize()); // ✅ stores weight in KG
        payment.setDriverId(request.getDriverId());
        payment.setCargoId(request.getCargoId());
        payment.setAmount(amountPaise);
        payment.setCurrency(razorpayClientService.getCurrency());
        payment.setRazorpayOrderId(orderId);
        payment.setStatus("CREATED");

        Payment saved = paymentRepository.save(payment);

        return new PaymentOrderResponse(
                saved.getRazorpayOrderId(),
                saved.getAmount(),
                saved.getCurrency(),
                razorpayClientService.getPublicKey(),
                saved.getId()
        );
    }

    public String verifyPayment(VerifyPaymentRequest request) {

        Optional<Payment> opt = paymentRepository.findById(request.getPaymentRecordId());
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Payment record not found.");
        }

        Payment payment = opt.get();

        // ✅ Razorpay signature verification (no SDK)
        // signature = HMAC_SHA256(orderId + "|" + paymentId, secret)
        String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
        String expectedSignature = hmacSha256Hex(payload, razorpayClientService.getSecret());

        if (!constantTimeEquals(expectedSignature, request.getRazorpaySignature())) {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            throw new IllegalArgumentException("Payment signature verification failed.");
        }

        payment.setRazorpayOrderId(request.getRazorpayOrderId());
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setStatus("PAID");
        paymentRepository.save(payment);

        return "Payment verified successfully";
    }

    private String hmacSha256Hex(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();

        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to verify signature.");
        }
    }

    // ✅ Avoid timing attacks (good practice)
    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
    public Optional<Payment> getPaymentByCargo(Long cargoId) {
    return paymentRepository.findByCargoId(cargoId);
}
}