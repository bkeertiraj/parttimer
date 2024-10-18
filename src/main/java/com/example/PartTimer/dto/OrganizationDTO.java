package com.example.PartTimer.dto;

import lombok.Data;

@Data
public class OrganizationDTO {
    private Long id;
    private String name;
    private Double expectedFee;

    public OrganizationDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public OrganizationDTO() {

    }
}
