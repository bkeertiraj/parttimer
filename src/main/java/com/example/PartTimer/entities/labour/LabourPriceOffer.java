package com.example.PartTimer.entities.labour;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class LabourPriceOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "labour_assignment_id")
    private LabourAssignment labourAssignment;

    @ManyToOne
    @JoinColumn(name = "labour_id")
    private Labour labour;

    private BigDecimal offeredPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
