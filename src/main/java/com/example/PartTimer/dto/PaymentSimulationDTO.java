package com.example.PartTimer.dto;

import lombok.Data;

@Data
public class PaymentSimulationDTO {
    private String paymentMethod; // "offline" or "bank"
    private String bankDetails; // Optional, for bank transfers
}
