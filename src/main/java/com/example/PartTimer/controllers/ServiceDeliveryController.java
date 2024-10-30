package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.ServiceDeliveryDTO;
import com.example.PartTimer.services.ServiceDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
@RequiredArgsConstructor
public class ServiceDeliveryController {

    private final ServiceDeliveryService serviceDeliveryService;

    @GetMapping("/{serviceId}")
    public ResponseEntity<List<ServiceDeliveryDTO>> getServiceRequests(@PathVariable Long serviceId) {
        List<ServiceDeliveryDTO> requests = serviceDeliveryService.getServiceRequests(serviceId);
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{requestId}")
    public ResponseEntity<ServiceDeliveryDTO> updateServiceRequest(
            @PathVariable Long requestId,
            @RequestBody ServiceDeliveryDTO updateRequest) {
        ServiceDeliveryDTO updated = serviceDeliveryService.updateServiceRequest(requestId, updateRequest);
        return ResponseEntity.ok(updated);
    }
}
