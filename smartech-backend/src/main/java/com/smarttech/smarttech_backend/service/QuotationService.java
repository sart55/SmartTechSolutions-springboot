package com.smarttech.smarttech_backend.service;

import com.smarttech.smarttech_backend.entity.Project;
import com.smarttech.smarttech_backend.entity.Quotation;
import com.smarttech.smarttech_backend.entity.QuotationItem;
import com.smarttech.smarttech_backend.repository.ProjectRepository;
import com.smarttech.smarttech_backend.repository.QuotationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final ProjectRepository projectRepository;

    public QuotationService(
            QuotationRepository quotationRepository,
            ProjectRepository projectRepository) {
        this.quotationRepository = quotationRepository;
        this.projectRepository = projectRepository;
    }

    public Quotation createQuotation(Long projectId, Quotation quotation) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        quotation.setProject(project);

        double itemsTotal = 0.0;

        if (quotation.getItems() != null) {
            for (QuotationItem item : quotation.getItems()) {

                item.setQuotation(quotation);

                double quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                double price = item.getPrice() != null ? item.getPrice() : 0;

                double subtotal = quantity * price;
                item.setSubtotal(subtotal);

                itemsTotal += subtotal;
            }
        }

        // Use setupCharges and devCharges from request
        double setup = quotation.getSetupCharges() != null ? quotation.getSetupCharges() : 0.0;
        double dev = quotation.getDevCharges() != null ? quotation.getDevCharges() : 0.0;

        double grandTotal = itemsTotal + setup + dev;
        quotation.setTotalAmount(grandTotal);

        project.setQuotationCreated(true);
        projectRepository.save(project);

        return quotationRepository.save(quotation);
    }

    @Transactional
    public void deleteAllQuotationsByProject(Long projectId) {
        quotationRepository.deleteByProject_Id(projectId);
    }



    public List<Quotation> getQuotationsByProjectId(Long projectId) {
        return quotationRepository.findByProject_Id(projectId);
    }


}