package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.ServiceManagementDTO;
import com.example.PartTimer.services.ServiceManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceManagementController {

    private final ServiceManagementService serviceManagementService;

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<ServiceManagementDTO>> getOrganizationServices
            (@PathVariable Long organizationId) {
        List<ServiceManagementDTO> services = serviceManagementService.getServicesForOrganization(organizationId);
        return ResponseEntity.ok(services);
    }
}
