package com.smarttech.smarttech_backend.repository;

import com.smarttech.smarttech_backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Fetch all payments by project ID
    List<Payment> findByProject_Id(Long projectId);
    void deleteByProject_Id(Long projectId);
    // Alternatively, you can use this:
    // List<Payment> findByProject(Project project);
}