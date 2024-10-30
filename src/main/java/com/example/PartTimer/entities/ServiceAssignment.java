package com.example.PartTimer.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServiceAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking; // Reference to the booking

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization; // Organization that picked up the booking

    @Column(nullable = false)
    private Double agreedPrice; // Price agreed with the organization

    @Column(nullable = true) // make nullable true if it may not be set initially
    private Double prevPrice; // Previous price before the agreed price


}
