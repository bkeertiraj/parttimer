package com.example.PartTimer.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
public class ServiceRequest { //not in use

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    private LocalDateTime requestDate;

    private String status;

    private String address;

    @Column(name = "organization_id")
    private Long organizationId; // Store the ID of the proposing organization

    @Column(name = "proposed_price")
    private double proposedPrice; // Store the proposed price directly in the ServiceRequest

    private String comments; // Store any additional comments

    private double ratings;

    // Additional fields for ratings and other metadata if needed
}
