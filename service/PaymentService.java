package com.smarttech.smarttech_backend.service;

import com.smarttech.smarttech_backend.entity.Payment;
import com.smarttech.smarttech_backend.entity.Project;
import com.smarttech.smarttech_backend.repository.PaymentRepository;
import com.smarttech.smarttech_backend.repository.ProjectRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ProjectRepository projectRepository;

    public PaymentService(PaymentRepository paymentRepository, ProjectRepository projectRepository) {
        this.paymentRepository = paymentRepository;
        this.projectRepository = projectRepository;
    }

    // Save payment with username of logged-in user
    public Payment savePayment(Long projectId, Payment payment, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        payment.setProject(project);
        payment.setUsername(username);          // Save the logged-in user's username
        payment.setCreatedAt(LocalDateTime.now()); // Optional: save created timestamp

        return paymentRepository.save(payment);
    }


    // Fetch all payments for a project
    public List<Payment> getPaymentsByProject(Long projectId) {
        return paymentRepository.findByProject_Id(projectId);
    }

    @Transactional
    public void deleteByProject_Id(Long projectId) {
        paymentRepository.deleteByProject_Id(projectId);
    }
}