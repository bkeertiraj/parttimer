package com.example.PartTimer.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
    private User user;

    @Column(nullable = true)
    private String name;  // Name of the person the service is for (could be different from user)

    @Column(nullable = false)
    private String email;  // Email of the person the service is for (could be different from user)

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private String time;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.POSTED;

    @ManyToMany
    @JoinTable(
            name = "booking_providers",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> serviceProviders = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column
    private Integer rating;

    @Column(length = 500)
    private String feedback;



}
