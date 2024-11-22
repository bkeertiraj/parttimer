package com.example.PartTimer.entities.country;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "us_cities")
public class UsCity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(nullable = false, length = 10)
    private String zipcode;

    @Column(length = 2)
    private String country = "US";

    @Column(name = "created_at")
    private ZonedDateTime createdAt;
}
