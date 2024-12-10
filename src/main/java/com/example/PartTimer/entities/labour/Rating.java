package com.example.PartTimer.entities.labour;

import com.example.PartTimer.entities.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "labour_id")
    private Labour labour;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Assuming you have a User entity

    @Column(nullable = false)
    private Integer ratingValue; // 1-5 star rating

    @Column(length = 500)
    private String review; // Optional review text

    private LocalDateTime ratedAt;
}
