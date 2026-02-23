package com.smarttech.smarttech_backend.controller;

import com.smarttech.smarttech_backend.entity.Payment;
import com.smarttech.smarttech_backend.service.PaymentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/project/{projectId}")
    public Payment addPayment(
            @PathVariable Long projectId,
            @RequestBody Payment payment,
            Authentication authentication) {

        String username = authentication.getName();

        return paymentService.savePayment(projectId, payment, username);
    }

    @DeleteMapping("/project/{projectId}/all")
    public void deleteAllPaymentsByProject(@PathVariable Long projectId) {
        paymentService.deleteByProject_Id(projectId);
    }

    @GetMapping("/project/{projectId}")
    public List<Payment> getPayments(@PathVariable Long projectId) {
        return paymentService.getPaymentsByProject(projectId);
    }
}