package com.smarttech.smarttech_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smarttech.smarttech_backend.entity.ComponentHistory;

import java.util.List;

@Repository
public interface ComponentHistoryRepository
        extends JpaRepository<ComponentHistory, Long> {

    // ✅ Sort by newest first
    List<ComponentHistory> findAllByOrderByDateDesc();
}