package com.example.PartTimer.controllers;


import com.example.PartTimer.dto.OrganizationSettingsServiceDTO;
import com.example.PartTimer.services.OrganizationServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organization-services")
public class OrganizationServiceController {

    private final OrganizationServiceService organizationServiceService;

    public OrganizationServiceController(OrganizationServiceService organizationServiceService) {
        this.organizationServiceService = organizationServiceService;
    }

    @GetMapping
    public List<OrganizationSettingsServiceDTO> getAllOrganizationServices() {
        return organizationServiceService.getAllOrganizationServices();
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<OrganizationSettingsServiceDTO>> getServicesByOrganization(@PathVariable Long id) {
        List<OrganizationSettingsServiceDTO> services = organizationServiceService.getServicesByOrganization(id);
        return ResponseEntity.ok(services);
    }

    @PatchMapping("/org/{organizationId}/service/{serviceId}/toggle-availability")
    public ResponseEntity<OrganizationSettingsServiceDTO> toggleServiceAvailability(
            @PathVariable Long organizationId,
            @PathVariable Long serviceId) {
        OrganizationSettingsServiceDTO serviceDTO = organizationServiceService.toggleServiceAvailability(organizationId, serviceId);
        return ResponseEntity.ok(serviceDTO);
    }
}
