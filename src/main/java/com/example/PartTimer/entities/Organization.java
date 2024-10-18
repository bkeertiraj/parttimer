package com.example.PartTimer.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "organization")
    private Set<User> members = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToMany
    @JoinColumn(name = "organization_id")
    private Set<CoOwner> coOwners = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "organization_services",
            joinColumns = @JoinColumn(name = "organization_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<Service> offeredServices = new HashSet<>();

    //new addition for expected fee field in organization_services
    @OneToMany(mappedBy = "organization")
    private Set<OrganizationService> organizationServices = new HashSet<>();

}
