package com.example.PartTimer.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private ServiceRequest serviceRequest; // foreign key

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String status; // paid, pending

    @Column(nullable = false)
    private LocalDate date;
}
