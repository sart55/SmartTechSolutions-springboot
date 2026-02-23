package com.smarttech.smarttech_backend.controller;

import com.smarttech.smarttech_backend.entity.Quotation;
import com.smarttech.smarttech_backend.service.QuotationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quotations")
@CrossOrigin(origins = "http://localhost:5173")
public class QuotationController {

    private final QuotationService quotationService;

    public QuotationController(QuotationService quotationService) {
        this.quotationService = quotationService;
    }

    @PostMapping("/project/{projectId}")
    public Quotation createQuotation(
            @PathVariable Long projectId,
            @RequestBody Quotation quotation) {

        return quotationService.createQuotation(projectId, quotation);
    }

    @GetMapping("/project/{projectId}")
    public List<Quotation> getByProject(@PathVariable Long projectId) {
        return quotationService.getQuotationsByProjectId(projectId);
    }

    @DeleteMapping("/project/{projectId}/all")
    public void deleteAllQuotationsByProject(
            @PathVariable Long projectId) {

        quotationService.deleteAllQuotationsByProject(projectId);
    }
}