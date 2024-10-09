package com.example.PartTimer.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // foreign key

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service; // foreign key

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner; // foreign key

    @Column(nullable = false)
    private String status; // ex: initiated, confirmed, in Progress, completed

    @Column(nullable = false)
    private String address; // where the service has to be performed

    @Column(nullable = false)
    private LocalDate date; // date of the service request

    @Column(nullable = false)
    private LocalTime time; // time of the service request

    @Column(nullable = false)
    private double estimatedRevenue;
}
