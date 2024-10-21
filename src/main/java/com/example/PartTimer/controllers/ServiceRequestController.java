package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.FeedbackDTO;
import com.example.PartTimer.dto.PaymentSimulationDTO;
import com.example.PartTimer.dto.ServiceRequestDTO;
import com.example.PartTimer.services.BookingService;
import com.example.PartTimer.services.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        System.out.println("Inside the get Service Request method, next is select organization");
        return ResponseEntity.ok(serviceRequestService.getServiceRequestDetails(id));
    }


    @PostMapping("/{id}/select-organization") //
    public ResponseEntity<ServiceRequestDTO> selectOrganization(
            @PathVariable Long id,
            @RequestBody Long organizationId) {
        try {
            System.out.println("Inside the select organization method, next is to confirm the service request");
            ServiceRequestDTO result = serviceRequestService.selectOrganization(id, organizationId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
//
//    @PutMapping("/{id}/status")
//    public ResponseEntity<ServiceRequestDTO> updateStatus(
//            @PathVariable Long id,
//            @RequestBody String status) {
//        System.out.println("Inside the updateStatus method, next is confirm service request");
//        return ResponseEntity.ok(serviceRequestService.updateStatus(id, status));
//    }
//

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ServiceRequestDTO> confirmServiceRequest(@PathVariable Long id, @RequestBody List<Long> assignedEmployeeIds) { //
        // Call the service to confirm the service request
        System.out.println("Inside confirm service request, next is initiate service request");
        ServiceRequestDTO updatedRequest = serviceRequestService.confirmAndAssignEmployees(id, assignedEmployeeIds);
        return ResponseEntity.ok(updatedRequest);
    }


    @PostMapping("/{id}/initiate")
    public ResponseEntity<ServiceRequestDTO> initiateServiceRequest(@PathVariable Long id) {
        // Call the service to confirm initiating the service request
        System.out.println("Inside confirm service request, next is to finish service request");
        //serviceRequestService.updateStatus(id, "INITIATED");
        return ResponseEntity.ok(serviceRequestService.updateStatus(id, "INITIATED"));
    }


    @PostMapping("/{id}/finish")
    public ResponseEntity<ServiceRequestDTO> finishServiceRequest(@PathVariable Long id) {
        // Call the service to confirm to finish the service request
        System.out.println("Inside confirm service request, next is simulate payment");
        //serviceRequestService.updateStatus(id, "INITIATED");
        return ResponseEntity.ok(serviceRequestService.updateStatus(id, "PAYMENT_PENDING"));
    }


    // Simulated payment endpoint
    @PostMapping("/{id}/payment/simulate")
    public ResponseEntity<ServiceRequestDTO> simulatePayment( //
            @PathVariable Long id,
            @RequestBody PaymentSimulationDTO paymentInfo) {
        System.out.println("Inside the simulatePayment method, next is verify payment");
        ServiceRequestDTO paymentResult = serviceRequestService.simulatePayment(id, paymentInfo);
        // After payment simulation, update status to PAYMENT_SUBMITTED
        //serviceRequestService.updateStatus(id, "PAYMENT_SUBMITTED");
        // Finally, mark as COMPLETED
        return ResponseEntity.ok(serviceRequestService.updateStatus(id, "PAYMENT_SUBMITTED"));
    }
    //verify the payment
    @PostMapping("/{id}/verify")
    public ResponseEntity<ServiceRequestDTO> verifyPayment(@PathVariable Long id) {
        // Call the service to confirm to finish the service request
        System.out.println("Inside verify payment, next is feedback");
        //serviceRequestService.updateStatus(id, "INITIATED");
        return ResponseEntity.ok(serviceRequestService.updateStatus(id, "COMPLETED"));
    }


    @PostMapping("/{id}/feedback")
    public ResponseEntity<ServiceRequestDTO> submitFeedback( //
            @PathVariable Long id,
            @RequestBody FeedbackDTO feedback) {
        System.out.println("Last, submitting feedback");
        return ResponseEntity.ok(serviceRequestService.submitFeedback(id, feedback));
    }

}
