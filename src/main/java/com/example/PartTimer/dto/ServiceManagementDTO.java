package com.example.PartTimer.dto;

import lombok.Data;

@Data
public class ServiceManagementDTO {
    private Long id;
    private String name;
    private String category;
    private String subcategory;
    private int pendingCount;
    private int completedCount;
    private int ongoingCount;
    private double revenue;
}
