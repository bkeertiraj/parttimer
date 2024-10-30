package com.example.PartTimer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrganizationSettingsServiceDTO {
    private Long id;
    private String name;
    private String location;
    private String category;
    private String subcategory;
    @JsonProperty("isAvailable")
    private boolean isAvailable;
}
