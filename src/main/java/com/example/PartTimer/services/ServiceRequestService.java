package com.example.PartTimer.services;

import com.example.PartTimer.dto.FeedbackDTO;
import com.example.PartTimer.dto.OrganizationDTO;
import com.example.PartTimer.dto.PaymentSimulationDTO;
import com.example.PartTimer.dto.ServiceRequestDTO;
import com.example.PartTimer.entities.*;
import com.example.PartTimer.repositories.BookingRepository;
import com.example.PartTimer.repositories.OrganizationRepository;
import com.example.PartTimer.repositories.OrganizationServiceRepository;
import com.example.PartTimer.repositories.ServiceAssignmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceRequestService {

    private final BookingRepository bookingRepository;
    private final ServiceAssignmentRepository serviceAssignmentRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationServiceRepository organizationServiceRepository;

    @Autowired
    public ServiceRequestService(
            BookingRepository bookingRepository,
            ServiceAssignmentRepository serviceAssignmentRepository,
            OrganizationRepository organizationRepository, OrganizationServiceRepository organizationServiceRepository) {
        this.bookingRepository = bookingRepository;
        this.serviceAssignmentRepository = serviceAssignmentRepository;
        this.organizationRepository = organizationRepository;
        this.organizationServiceRepository = organizationServiceRepository;
    }

    public ServiceRequestDTO getServiceRequestDetails(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

//        ServiceAssignment assignment = serviceAssignmentRepository.findByBooking(booking)
//                .orElse(null);

        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setId(booking.getBookingId());
        dto.setStatus(booking.getStatus().toString());
        dto.setAddress(booking.getLocation());
        dto.setDate(LocalDate.parse(booking.getDate()));
        dto.setTime(LocalTime.parse(booking.getTime()));
        dto.setPaymentStatus(PaymentStatus.valueOf(booking.getPaymentStatus().toString()));

        // Check if the service is already assigned to an organization
        ServiceAssignment assignment = serviceAssignmentRepository.findByBooking(booking)
                .orElse(null);

        if (assignment != null) {
            dto.setOrganizationId(assignment.getOrganization().getId());
            dto.setOrganizationName(assignment.getOrganization().getName());
            dto.setAgreedPrice(assignment.getAgreedPrice());
        }
        else {
            // If no organization is assigned, fetch organizations offering the booked service
            List<OrganizationDTO> availableOrganizations = getAvailableOrganizationsForService(booking.getService().getServiceId());
            dto.setAvailableOrganizations(availableOrganizations);
        }
        return dto;
    }

    private List<OrganizationDTO> getAvailableOrganizationsForService(Long serviceId) {
        List<OrganizationService> organizationServices = organizationServiceRepository.findOrganizationsByServiceId(serviceId);

        List<OrganizationDTO> organizationDTOs = new ArrayList<>();

        for (OrganizationService orgService : organizationServices) {
            OrganizationDTO dto = new OrganizationDTO();
            dto.setId(orgService.getOrganization().getId());
            dto.setName(orgService.getOrganization().getName());
            dto.setExpectedFee(orgService.getExpectedFee()); // Set the expected fee
            organizationDTOs.add(dto);
        }

        return organizationDTOs;
    }

    public ServiceRequestDTO selectOrganization(Long bookingId, Long organizationId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Create service assignment
        ServiceAssignment assignment = new ServiceAssignment();
        assignment.setBooking(booking);
        assignment.setOrganization(organization);

        // Get expected fee from OrganizationService
        Double agreedPrice = organization.getOrganizationServices().stream()
                .filter(os -> os.getService().equals(booking.getService()))
                .findFirst()
                .map(OrganizationService::getExpectedFee)
                .orElse(booking.getService().getBaseFee());

        assignment.setAgreedPrice(agreedPrice);
        serviceAssignmentRepository.save(assignment);

        // Update booking status
        booking.setStatus(BookingStatus.CONFIRMED); //
        bookingRepository.save(booking);

        return getServiceRequestDetails(bookingId);
    }

    public ServiceRequestDTO simulatePayment(Long id, PaymentSimulationDTO paymentInfo) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Simulate payment processing delay
        try {
            Thread.sleep(2000); // 2 second delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Update payment status
//        booking.setPaymentStatus(PaymentStatus.COMPLETED);
        booking.setStatus(BookingStatus.REQUEST_SENT);
        bookingRepository.save(booking);

        return getServiceRequestDetails(id);
    }

    public ServiceRequestDTO updateStatus(Long id, String status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Ensure the provided status is valid and exists in the enum
        try {
            BookingStatus newStatus = BookingStatus.valueOf(status.trim().toUpperCase());
            booking.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status);
        }

        // Persist the updated booking
        bookingRepository.save(booking);

        // Retrieve the service assignment based on the booking
        ServiceAssignment serviceAssignment = serviceAssignmentRepository.findById(booking.getBookingId())
                .orElseThrow(() -> new RuntimeException("Service assignment not found for booking ID: " + booking.getBookingId()));

        // Get the assigned organization
        Organization organization = serviceAssignment.getOrganization();
// For Owners
        List<String> ownerNames = new ArrayList<>();
        Owner owner = organization.getOwner(); // Check if this returns a single Owner
        if (owner != null) {
            ownerNames.add(owner.getFirstName());
        }

// For Co-Owners
        List<String> coOwnerNames = organization.getCoOwners().stream() // Assuming this returns a collection
                .map(CoOwner::getFirstName) // Assuming getName() exists in CoOwner
                .collect(Collectors.toList());

        // Create and populate the DTO with owners and co-owners
        ServiceRequestDTO serviceRequestDTO = mapToServiceRequestDTO(booking);
        serviceRequestDTO.setOwnerNames(ownerNames);
        serviceRequestDTO.setCoOwnerNames(coOwnerNames);

        // Return the updated DTO after mapping
        return mapToServiceRequestDTO(booking);

    }

    private ServiceRequestDTO mapToServiceRequestDTO(Booking booking) {
        ServiceRequestDTO dto = new ServiceRequestDTO();

        // Basic Booking information
        dto.setId(booking.getBookingId());
        dto.setUserId(booking.getUser().getUserId()); // Assuming Booking has a relationship with User
        dto.setServiceId(booking.getService().getServiceId()); // Assuming Booking has a relationship with Service
        //dto.setRequestDate(LocalDate.parse(booking.getDate()));
        dto.setStatus(booking.getStatus().toString());
        dto.setAddress(booking.getLocation());
        dto.setDate(LocalDate.parse(booking.getDate())); // Assuming bookingDate holds both date and time
        dto.setTime(LocalTime.parse(booking.getTime()));
        dto.setPaymentStatus(PaymentStatus.valueOf(booking.getPaymentStatus().toString()));

        // If service assignment exists, include organization and price
        ServiceAssignment assignment = serviceAssignmentRepository.findByBooking(booking).orElse(null);
        if (assignment != null) {
            dto.setOrganizationId(assignment.getOrganization().getId());
            dto.setOrganizationName(assignment.getOrganization().getName());
            dto.setAgreedPrice(assignment.getAgreedPrice());
        }

        // Comments, Rating, and Feedback
        dto.setFeedback(booking.getFeedback());
        dto.setRating(booking.getRating());
        dto.setFeedback(booking.getFeedback());

        return dto;
    }


    public ServiceRequestDTO confirmServiceRequest(Long id) {
        // Find the booking by ID
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Update the booking status to "confirmed" or whatever status you want to set
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking); // Persist the change

        // Return the updated ServiceRequestDTO
        return mapToServiceRequestDTO(booking);
    }

    public ServiceRequestDTO submitFeedback(Long id, FeedbackDTO feedback) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Set feedback details in the booking entity
        booking.setRating(feedback.getRating());
        booking.setFeedback(feedback.getFeedback());

        bookingRepository.save(booking); // Save the updated booking with feedback

        // Return the updated DTO
        return mapToServiceRequestDTO(booking);
    }

}
