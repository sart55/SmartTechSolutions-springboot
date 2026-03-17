package com.smarttech.smarttech_backend.controller;

import com.smarttech.smarttech_backend.dto.PaymentSummary;
import com.smarttech.smarttech_backend.entity.Payment;
import com.smarttech.smarttech_backend.entity.Project;
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

    @GetMapping("/project/{projectId}/summary")
    public PaymentSummary getPaymentSummary(@PathVariable Long projectId) {
        return paymentService.getPaymentSummary(projectId);
    }

    @PostMapping("/project/{projectId}/total")
    public Project saveTotalAmount(
            @PathVariable Long projectId,
            @RequestBody Project requestBody) {

        return paymentService.saveProjectTotalAmount(projectId, requestBody.getTotalAmount());
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
