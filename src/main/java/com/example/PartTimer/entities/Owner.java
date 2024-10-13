package com.example.PartTimer.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = {"employees", "assignedOwners"})
@EqualsAndHashCode(callSuper = true, exclude = {"employees", "assignedOwners"})
@DiscriminatorValue("Owner")
public class Owner extends Employee {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long ownerId;

//    @Column(nullable = false)
//    private String name;
//
//    @Column(unique = true, nullable = true)
//    private String email;

    @Column(nullable = true)
    private String password;

    @Column
    private String phoneNumber;

    @JsonIgnore
    @ManyToMany(mappedBy = "owners")
    private Set<Employee> employees = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "owner_assignments",
            joinColumns = @JoinColumn(name = "owner_id", referencedColumnName = "employeeId"),
            inverseJoinColumns = @JoinColumn(name = "assigned_owner_id", referencedColumnName = "employeeId")
    )
    private Set<Owner> assignedOwners = new HashSet<>(); //other owners that this owner can assign tasks to
}
