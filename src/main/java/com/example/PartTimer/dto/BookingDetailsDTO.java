package com.example.PartTimer.dto;

import com.example.PartTimer.entities.User;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class BookingDetailsDTO {
    private String id;
    private String name;
    private String status;
    private String description;
    private String date;
    private String time;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String clientEmail;
    private Double agreedPrice;
    private Set<User> assignedEmployees;
    private List<String> pastOfferedPrices;

    private String location;
}
