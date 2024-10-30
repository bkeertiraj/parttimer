package com.example.PartTimer.services;

import com.example.PartTimer.dto.OrganizationSettingsServiceDTO;
import com.example.PartTimer.entities.OrganizationService;
import com.example.PartTimer.repositories.OrganizationServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationServiceService {

    private final OrganizationServiceRepository organizationServiceRepository;

    public OrganizationServiceService(OrganizationServiceRepository organizationServiceRepository) {
        this.organizationServiceRepository = organizationServiceRepository;
    }


    public List<OrganizationSettingsServiceDTO> getAllOrganizationServices() {
        List<OrganizationService> organizationServices = organizationServiceRepository.findAll();
        return organizationServices.stream()
                .map(this::convertToOrganizationServiceDTO)
                .collect(Collectors.toList());
    }

    private OrganizationSettingsServiceDTO convertToOrganizationServiceDTO(OrganizationService organizationService) {
        OrganizationSettingsServiceDTO serviceDTO = new OrganizationSettingsServiceDTO();
        serviceDTO.setId(organizationService.getService().getServiceId());
        System.out.println("From convertToOrganizationServiceDTO, id: " +organizationService.getId());
        serviceDTO.setName(organizationService.getService().getName());
        serviceDTO.setLocation(organizationService.getOrganization().getName());
        serviceDTO.setCategory(organizationService.getService().getCategory());
        serviceDTO.setSubcategory(organizationService.getService().getSubcategory());
        serviceDTO.setAvailable(organizationService.isEnabled());
        return serviceDTO;
    }

    public List<OrganizationSettingsServiceDTO> getServicesByOrganization(Long id) {
        List<OrganizationService> organizationServices = organizationServiceRepository.findByOrganizationId(id);
        return organizationServices.stream()
                .map(this::convertToOrganizationServiceDTO)
                .collect(Collectors.toList());
    }

    public OrganizationSettingsServiceDTO toggleServiceAvailability(Long organizationId, Long serviceId) {
        System.out.println("Organization ID: "+organizationId + ", Service ID: "+serviceId);
        OrganizationService organizationService = organizationServiceRepository.
                findByOrganizationIdAndServiceId(organizationId, serviceId)
                .orElseThrow(() -> new RuntimeException("OrganizationService not found"));
        organizationService.setEnabled(!organizationService.isEnabled());
        OrganizationService savedOrganizationService = organizationServiceRepository.save(organizationService);
        return convertToOrganizationServiceDTO(savedOrganizationService);
    }
}
