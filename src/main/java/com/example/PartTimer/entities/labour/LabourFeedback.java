package com.example.PartTimer.entities.labour;

import com.example.PartTimer.entities.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class LabourFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // The user giving or receiving feedback

    @ManyToOne
    @JoinColumn(name = "labour_id")
    private Labour labour; // The labour giving or receiving feedback

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private LabourBooking booking; // Link feedback to a specific booking

    @Column(nullable = false)
    private Integer ratingValue; // 1-5 star rating

    @Column(length = 500)
    private String review; // Optional review text

    @Enumerated(EnumType.STRING)
    private LabourFeedbackType feedbackType; // USER_TO_LABOUR or LABOUR_TO_USER

    @Column(nullable = false)
    private LocalDateTime feedbackDate;
}
