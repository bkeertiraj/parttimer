package com.example.PartTimer.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrganizationServiceDTO {
    private Long id;
    private String serviceName;
    private Double expectedFee;
}
