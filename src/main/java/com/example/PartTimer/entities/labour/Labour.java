package com.example.PartTimer.entities.labour;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Labour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;
    private String title;
    private String firstName;
    private String middleName;
    private String lastName;
    private String password;

    @ElementCollection
    private List<String> serviceZipCodes; // Instead of zip1, zip2, zip3

    @Enumerated(EnumType.STRING)
    private LabourSubscriptionStatus subscriptionStatus;

    @OneToMany(mappedBy = "labour")
    private List<Rating> ratings;

    @Transient // Calculated field, not stored in database
    private Double averageRating;

    // Method to calculate average rating
    public Double getAverageRating() {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToDouble(Rating::getRatingValue)
                .average()
                .orElse(0.0);
    }

    private Boolean isRideNeeded = false;
}
