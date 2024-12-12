package com.example.PartTimer.entities.labour;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class LabourAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    @JsonBackReference
    private LabourBooking booking;

    @ManyToOne
    @JoinColumn(name = "labour_id")
    private Labour labour;

    private LocalDate bookingDate;
    private String timeSlot;
    private String bookingNote;

    @Enumerated(EnumType.STRING)
    private LabourBookingStatus bookingStatus;

    // Potential additional fields
    private BigDecimal proposedPrice;
    private LocalDateTime statusChangedAt;
}
