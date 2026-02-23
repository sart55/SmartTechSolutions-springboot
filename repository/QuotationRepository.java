package com.smarttech.smarttech_backend.repository;

import com.smarttech.smarttech_backend.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    List<Quotation> findByProject_Id(Long projectId);

    void deleteByProject_Id(Long projectId);
}