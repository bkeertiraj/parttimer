package com.example.PartTimer.entities.labour;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class LabourAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
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
