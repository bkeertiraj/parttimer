package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.dashboard.*;
import com.example.PartTimer.entities.BookingStatus;
import com.example.PartTimer.services.DashboardOrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organization")
public class OrganizationController {

    private final DashboardOrganizationService dborganizationService;

    public OrganizationController(DashboardOrganizationService dborganizationService) {
        this.dborganizationService = dborganizationService;
    }

    @GetMapping("/{orgId}/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats(@PathVariable Long orgId) {
        return ResponseEntity.ok(dborganizationService.getDashboardStats(orgId));
    }

    @GetMapping("/{orgId}/employees")
    public ResponseEntity<List<DashboardUserDTO>> getEmployees(@PathVariable Long orgId) {
        return ResponseEntity.ok(dborganizationService.getEmployees(orgId));
    }

    @GetMapping("/{orgId}/services")
    public ResponseEntity<List<OrganizationServiceDTO>> getServices(@PathVariable Long orgId) {
        return ResponseEntity.ok(dborganizationService.getOrganizationServices(orgId));
    }

    @GetMapping("/{orgId}/bookings")
    public ResponseEntity<List<DashboardBookingDTO>> getBookings(
            @PathVariable Long orgId,
            @RequestParam(required = false) BookingStatus status
    ) {
        return ResponseEntity.ok(dborganizationService.getBookings(orgId, status));
    }

    @GetMapping("{orgId}/latest-bookings")
    public ResponseEntity<List<DashboardBookingDTO>> latestBookings(
            @PathVariable Long orgId) {
        return ResponseEntity.ok(dborganizationService.getLatestBookingsById(orgId));
    }

    @GetMapping("/{orgId}/service-assignments")
    public ResponseEntity<List<DashboardServiceAssignmentDTO>> getServiceAssignments(@PathVariable Long orgId) {
        return ResponseEntity.ok(dborganizationService.getServiceAssignments(orgId));
    }

    @PutMapping("/{orgId}/services/{serviceId}")
    public ResponseEntity<OrganizationServiceDTO> updateServiceFee(
            @PathVariable Long orgId,
            @PathVariable Long serviceId,
            @RequestBody Map<String, Double> request
    ) {
        return ResponseEntity.ok(dborganizationService.updateServiceFee(orgId, serviceId, request.get("expectedFee")));
    }

    @PostMapping("/{orgId}/employees")
    public ResponseEntity<DashboardUserDTO> addEmployee
            (@PathVariable Long orgId, @RequestBody DashboardUserDTO employeeDTO) {
        return ResponseEntity.ok(dborganizationService.addEmployee(orgId, employeeDTO));
    }
}
