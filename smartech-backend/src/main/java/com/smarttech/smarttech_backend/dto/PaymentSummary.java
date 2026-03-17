package com.smarttech.smarttech_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentSummary {

    private Double totalAmount;
    private Double totalPaid;
    private Double remainingAmount;
}
