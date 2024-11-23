package com.example.PartTimer.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")  // Rename table to reflect that all accounts are users
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type", discriminatorType = DiscriminatorType.STRING)
public class User { //renaming the class to User from Employee

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; //rename to userId

    @Column(length = 10)
    private String title;

    @Column(nullable = false)
    private String firstName;

    @Column
    private String middleName;  // Optional

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password; //new password field

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;  // new address field


    @Column(name = "country") //name = "country", length = 2
    private String country;  // Country code (e.g., US)

    @Column(name = "state")
    private String state;    // State name

    @Column(name = "city")
    private String city;     // City name

    @Column(name = "zipcode")
    private String zipcode;  // Postal/ZIP code

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole = UserRole.USER; //default to USER

    @ManyToOne
    @JoinColumn(name = "organization_id")
    @JsonIgnore
    private Organization organization;

    @CreationTimestamp
    @Column(name = "join_date", updatable = false)
    private LocalDate joinDate;

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "user_service",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<Service> services = new HashSet<>();

    // removed fields: designation, status, isAdmin, roleType, owners

    public String getFullName() {
        StringBuilder fullName = new StringBuilder(firstName);
        if(middleName!=null && !middleName.isEmpty()) {
            fullName.append(" ").append(middleName);
        }
        fullName.append(" ").append(lastName);
        return fullName.toString();
    }

    public boolean isCoOwner() {
        return UserRole.CO_OWNER.equals(this.userRole);
    }
}

