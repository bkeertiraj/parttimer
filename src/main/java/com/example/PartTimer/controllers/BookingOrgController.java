package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.*;
import com.example.PartTimer.services.BookingAssignmentService;
import com.example.PartTimer.services.BookingOrgService;
import com.example.PartTimer.services.BookingService;
import com.example.PartTimer.services.ServiceAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/organizations/{orgId}/bookings")
public class BookingOrgController {

    private final BookingOrgService bookingOrgService;
    private final ServiceAssignmentService serviceAssignmentService;
    private final BookingAssignmentService bookingAssignmentService;
    private final BookingService bookingService;

    public BookingOrgController(BookingOrgService bookingOrgService, ServiceAssignmentService serviceAssignmentService, BookingAssignmentService bookingAssignmentService, BookingService bookingService) {
        this.bookingOrgService = bookingOrgService;
        this.serviceAssignmentService = serviceAssignmentService;
        this.bookingAssignmentService = bookingAssignmentService;
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDetailsDTO> getBookingDetails(
            @PathVariable Long orgId,
            @PathVariable Long bookingId
    ) throws AccessDeniedException {
        return ResponseEntity.ok(bookingOrgService.getBookingDetails(orgId, bookingId));
    }

    @PostMapping("/{bookingId}/price-offer")
    public ResponseEntity<ServiceAssignmentDTO> offerPrice(
            @PathVariable Long orgId,
            @PathVariable Long bookingId,
            @RequestBody PriceOfferRequest request) {
        return ResponseEntity.ok(serviceAssignmentService.createOrUpdatePriceOffer(orgId, bookingId, request.getOfferedPrice()));
    }


    @GetMapping("/{bookingId}/available-employees")
    public ResponseEntity<OrganizationEmployeeDTO> getAvailableEmployees(
            @PathVariable Long orgId,
            @PathVariable Long bookingId
    ) {
        OrganizationEmployeeDTO dto = serviceAssignmentService.getOrganizationEmployeeDetails(orgId, bookingId);
        return ResponseEntity.ok(dto);
    }


    @PostMapping("/{bookingId}/assign-employees")
    public ResponseEntity<BookingAssignmentDTO> assignEmployeesAndConfirmRequest(
            @PathVariable Long orgId,
            @PathVariable Long bookingId,
            @RequestBody EmployeeAssignmentRequest request) {
        return ResponseEntity.ok(serviceAssignmentService.assignEmployees(orgId, bookingId, request.getEmployeeIds()));
    }


    @PostMapping("/{bookingId}/initiate-request")
    public ResponseEntity<BookingAssignmentDTO> initiateServiceRequest(@PathVariable Long bookingId) {
        System.out.println("Inside initiate service request, next is to finish service request");
        return ResponseEntity.ok(serviceAssignmentService.updateStatus(bookingId, "INITIATED"));
    }

    @PostMapping("/{bookingId}/finish-request")
    public ResponseEntity<BookingAssignmentDTO> completeWorkAndRequestPayment(@PathVariable Long bookingId) {
        System.out.println("Inside complete work, next is  payment");
        return ResponseEntity.ok(serviceAssignmentService.updateStatus(bookingId, "PAYMENT_PENDING"));
    }

    @PostMapping("/{bookingId}/verify-payment")
    public ResponseEntity<BookingAssignmentDTO> verifyPayment(@PathVariable Long bookingId) {
        System.out.println("Inside verify payment, next is to review the user");
        return ResponseEntity.ok(serviceAssignmentService.updateStatus(bookingId, "COMPLETED"));
    }

    @PostMapping("/{bookingId}/user-feedback")
    public ResponseEntity<BookingAssignmentDTO> submitFeedback(
            @PathVariable Long bookingId,
            @RequestBody OrgFeedbackDTO feedback
    ) {
        System.out.println("Last, submitting the user feedback");
        return ResponseEntity.ok(serviceAssignmentService.submitFeedback(bookingId, feedback));
    }


}
