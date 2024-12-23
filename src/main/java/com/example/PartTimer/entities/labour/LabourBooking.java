package com.example.PartTimer.entities.labour;

import com.example.PartTimer.entities.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class LabourBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;
    private String phoneNumber;
    private String email;
    private String zipcode;
    private String city;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // This establishes the link between booking and user


    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<LabourAssignment> labourAssignments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
