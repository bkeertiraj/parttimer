package com.example.PartTimer.entities.labour;

import com.example.PartTimer.entities.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class LabourBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;
    private String phoneNumber;
    private String email;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // This establishes the link between booking and user


    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LabourAssignment> labourAssignments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
