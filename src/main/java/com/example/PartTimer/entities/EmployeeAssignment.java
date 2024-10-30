package com.example.PartTimer.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class EmployeeAssignment { //no more in use

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private ServiceRequest serviceRequest; // foreign key

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee; // foreign key

    @Column(nullable = false)
    private String status;
}
