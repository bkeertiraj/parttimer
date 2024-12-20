package com.example.PartTimer.dto.labour;

import com.example.PartTimer.entities.labour.LabourPriceOfferStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LabourPriceHistoryDTO {

    private Long offerId;
    private Long assignmentId;
    private Long bookingId;
    private String bookingAddress;
    private String timeSlot;
    private BigDecimal offeredPrice;
    private LabourPriceOfferStatus status;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
private LocalDate bookingDate;
    private String city;
    private String zipCode;
}
