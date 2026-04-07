package com.edutech.logisticsmanagementandtrackingsystem.Controller;

import com.edutech.logisticsmanagementandtrackingsystem.dto.PaymentOrderResponse;
import com.edutech.logisticsmanagementandtrackingsystem.dto.PaymentRequest;
import com.edutech.logisticsmanagementandtrackingsystem.dto.VerifyPaymentRequest;
import com.edutech.logisticsmanagementandtrackingsystem.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResponse> createOrder(@RequestBody PaymentRequest request) throws Exception {
        return ResponseEntity.ok(paymentService.createOrder(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody VerifyPaymentRequest request) throws Exception {
        return ResponseEntity.ok(paymentService.verifyPayment(request));
    }
    @GetMapping("/by-cargo/{cargoId}")
public ResponseEntity<?> getPaymentByCargo(@PathVariable Long cargoId) {
    return paymentService.getPaymentByCargo(cargoId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.ok(null));
}

}
