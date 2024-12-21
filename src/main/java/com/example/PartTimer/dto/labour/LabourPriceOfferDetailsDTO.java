package com.example.PartTimer.dto.labour;

import com.example.PartTimer.entities.labour.LabourPriceOfferStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LabourPriceOfferDetailsDTO {
    private LocalDate bookingDate;
    private String bookingNote;
    private LabourPriceOfferStatus bookingStatus;
    private String timeSlot;
    private String userPhoneNumber;
    private String userEmail;
    private String bookingAddress; // Only for ACCEPTED
    private BigDecimal acceptedPrice;  // Only for WITHDRAWN
    private Double acceptedLabourRating; // Only for WITHDRAWN
    private LabourPriceOfferStatus status;
}
