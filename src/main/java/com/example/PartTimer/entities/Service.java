package com.example.PartTimer.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category; // ex: Automotive, Home & Garden, Other

    @Column(nullable = false)
    private double baseFee;

    @Column(nullable = false)
    private String subcategory; // ex: cleaning, maintenance, lawncare, landscaping, others

    @Column
    private String description;

    @ManyToMany(mappedBy = "services", fetch = FetchType.EAGER)
    private Set<Employee> employees = new HashSet<>();
}
