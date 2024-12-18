package com.example.PartTimer.dto.labour;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LabourPriceOfferDTO {
    private Long labourAssignmentId;
    private BigDecimal proposedPrice;
    private String notes;
}
