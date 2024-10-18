package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.FeedbackDTO;
import com.example.PartTimer.dto.PaymentSimulationDTO;
import com.example.PartTimer.dto.ServiceRequestDTO;
import com.example.PartTimer.services.BookingService;
import com.example.PartTimer.services.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "http://localhost:5173")
public class ServiceRequestController {

    private BookingService bookingService;

    public ServiceRequestController(BookingService bookingService, ServiceRequestService serviceRequestService) {
        this.bookingService = bookingService;
        this.serviceRequestService = serviceRequestService;
    }

    private final ServiceRequestService serviceRequestService;

    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequestDTO> getServiceRequest(@PathVariable Long id) {
        return ResponseEntity.ok(serviceRequestService.getServiceRequestDetails(id));
    }


    @PostMapping("/{id}/select-organization")
    public ResponseEntity<ServiceRequestDTO> selectOrganization(
            @PathVariable Long id,
            @RequestBody Long organizationId) {
        try {

            ServiceRequestDTO result = serviceRequestService.selectOrganization(id, organizationId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ServiceRequestDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody String status) {
        return ResponseEntity.ok(serviceRequestService.updateStatus(id, status));
    }

    // Simulated payment endpoint
    @PostMapping("/{id}/payment/simulate")
    public ResponseEntity<ServiceRequestDTO> simulatePayment(
            @PathVariable Long id,
            @RequestBody PaymentSimulationDTO paymentInfo) {
        return ResponseEntity.ok(serviceRequestService.simulatePayment(id, paymentInfo));
    }

    @PostMapping("/{id}/feedback")
    public ResponseEntity<ServiceRequestDTO> submitFeedback(
            @PathVariable Long id,
            @RequestBody FeedbackDTO feedback) {
        return ResponseEntity.ok(serviceRequestService.submitFeedback(id, feedback));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ServiceRequestDTO> confirmServiceRequest(@PathVariable Long id) {
        // Call the service to confirm the service request
        ServiceRequestDTO updatedRequest = serviceRequestService.confirmServiceRequest(id);
        return ResponseEntity.ok(updatedRequest);
    }

//
//
//    @GetMapping("/{requestId}")
//    public ResponseEntity<ServiceRequestDTO> getServiceRequestById(@PathVariable Long requestId) {
//        ServiceRequestDTO serviceRequest = bookingService.getServiceRequestById(requestId);
//        return ResponseEntity.ok(serviceRequest);
//    }
}
