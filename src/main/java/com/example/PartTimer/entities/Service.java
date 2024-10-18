package com.example.PartTimer.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

//    @ManyToMany
//    @JoinTable(
//            name = "organization_services", // Use the same join table as in Organization
//            joinColumns = @JoinColumn(name = "service_id"),
//            inverseJoinColumns = @JoinColumn(name = "organization_id")
//    )
//    private Set<Organization> offeredByOrganizations = new HashSet<>();
//
//    //new addition for expected fee field in organization_services
//    @OneToMany(mappedBy = "service")
//    private Set<OrganizationService> organizationServices = new HashSet<>();
}
