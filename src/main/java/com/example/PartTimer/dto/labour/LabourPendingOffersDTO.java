package com.example.PartTimer.dto.labour;

import com.example.PartTimer.entities.labour.LabourPriceOfferStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabourPendingOffersDTO {
    private Long offerId;
    private Long labourAssignmentId;
    private Long labourId;
    private Long bookingId;
    private BigDecimal offeredPrice;
    private LabourPriceOfferStatus status;
    private String bookingAddress;
    private String bookingNote;
    private String bookingDate;
    private String timeSlot;
    private LocalDateTime offerCreatedAt;
}
