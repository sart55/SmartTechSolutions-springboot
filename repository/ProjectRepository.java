package com.smarttech.smarttech_backend.repository;

import com.smarttech.smarttech_backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    // ✅ Check duplicate using project name + contact
    Optional<Project> findByProjectNameAndCustomerContact(
            String projectName,
            String customerContact
    );

    boolean existsByProjectNameAndCustomerContact(
            String projectName,
            String customerContact
    );

    boolean existsByProjectNameAndCustomerContactAndIdNot(
            String projectName,
            String customerContact,
            Long id
    );
}