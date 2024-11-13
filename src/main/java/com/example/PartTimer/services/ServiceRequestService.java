package com.example.PartTimer.services;

import com.example.PartTimer.dto.*;
import com.example.PartTimer.entities.*;
import com.example.PartTimer.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceRequestService {

    private final BookingRepository bookingRepository;
    private final ServiceAssignmentRepository serviceAssignmentRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationServiceRepository organizationServiceRepository;
    private final UserRepository userRepository;
    private final BookingAssignmentRepository bookingAssignmentRepository;

    @Autowired
    public ServiceRequestService(UserRepository userRepository,
                                 BookingRepository bookingRepository,
                                 ServiceAssignmentRepository serviceAssignmentRepository,
                                 OrganizationRepository organizationRepository, OrganizationServiceRepository organizationServiceRepository, BookingAssignmentRepository bookingAssignmentRepository) {
        this.bookingRepository = bookingRepository;
        this.serviceAssignmentRepository = serviceAssignmentRepository;
        this.organizationRepository = organizationRepository;
        this.organizationServiceRepository = organizationServiceRepository;
        this.userRepository = userRepository;
        this.bookingAssignmentRepository = bookingAssignmentRepository;
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

            BookingAssignment bookingAssignment = bookingAssignmentRepository
                    .findByBookingBookingId(booking.getBookingId())
                    .orElse(null);
            if (bookingAssignment != null && bookingAssignment.getAssignedEmployees() != null) {
                List<EmployeeDTO> employeesInvolved = bookingAssignment.getAssignedEmployees().stream()
                        .map(this::mapToEmployeeDTO)
                        .collect(Collectors.toList());
                dto.setEmployeesInvolved(employeesInvolved);
            }

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
        booking.setStatus(BookingStatus.SELLER_SELECTED);
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
        booking.setStatus(BookingStatus.SELLER_SELECTED);
        booking.setPaymentStatus(PaymentStatus.COMPLETED);
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
        ServiceAssignment serviceAssignment = serviceAssignmentRepository.findByBooking(booking)
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
//        ServiceRequestDTO serviceRequestDTO = mapToServiceRequestDTO(booking);
//        serviceRequestDTO.setOwnerNames(ownerNames);
//        serviceRequestDTO.setCoOwnerNames(coOwnerNames);

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

        BookingAssignment bookingAssignment = bookingAssignmentRepository
                .findByBookingBookingId(booking.getBookingId())
                .orElse(null);

        if (assignment != null) {
            dto.setOrganizationId(assignment.getOrganization().getId());
            dto.setOrganizationName(assignment.getOrganization().getName());
            dto.setAgreedPrice(assignment.getAgreedPrice());

            // If booking assignment exists, set the employees involved
            if (bookingAssignment != null && bookingAssignment.getAssignedEmployees() != null) {
                List<EmployeeDTO> employeesInvolved = bookingAssignment.getAssignedEmployees().stream()
                        .map(this::mapToEmployeeDTO)
                        .collect(Collectors.toList());
                dto.setEmployeesInvolved(employeesInvolved);
            }
        }

        // Comments, Rating, and Feedback
        dto.setFeedback(booking.getFeedback());
        dto.setRating(booking.getRating());
        dto.setFeedback(booking.getFeedback());

        dto.setFullName(booking.getUser().getFullName());
        dto.setRole(booking.getUser().getUserRole());
        return dto;
    }


    public ServiceRequestDTO confirmServiceRequest(Long id) {
        // Find the booking by ID
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Update the booking status to "confirmed" or whatever status you want to set
        booking.setStatus(BookingStatus.SELLER_ACCEPTED);
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

    public ServiceRequestDTO confirmAndAssignEmployees(Long id, List<Long> assignedEmployeeIds) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.SELLER_ACCEPTED);
        List<User> assignedEmployees = userRepository.findAllById(assignedEmployeeIds);

        //new code from here
        ServiceAssignment serviceAssignment = serviceAssignmentRepository.findByBooking(booking).orElse(null);

        BookingAssignment bookingAssignment = new BookingAssignment();
        bookingAssignment.setBooking(booking);
        bookingAssignment.setOrganization(serviceAssignment.getOrganization());
        bookingAssignment.setAgreedPrice(serviceAssignment.getAgreedPrice());
        bookingAssignment.setAssignedEmployees(new HashSet<>(assignedEmployees));

        bookingAssignment = bookingAssignmentRepository.save(bookingAssignment);
//
        List<EmployeeDTO> employeesInvolved = assignedEmployees.stream()
                .map(this::mapToEmployeeDTO)
                .collect(Collectors.toList());
        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setEmployeesInvolved(employeesInvolved);
//
        return mapToDTO(booking, bookingAssignment);

//        // Print the names of the assigned employees
//        assignedEmployees.forEach(employee -> {
//            System.out.println("Assigned Employee: " + employee.getFullName());
//            // You can use a logger instead of System.out.println
//            // log.debug("Assigned Employee: {} {}", employee.getFirstName(), employee.getLastName());
//        });
//
//        booking.setServiceProviders(new HashSet<>(assignedEmployees));
//
//        booking = bookingRepository.save(booking);
//
//        ServiceRequestDTO dto = mapToDTO(booking);
//
//        List<EmployeeDTO> employeesInvolved = assignedEmployees.stream()
//                .map(this::mapToEmployeeDTO)
//                .collect(Collectors.toList());
//
//        dto.setEmployeesInvolved(employeesInvolved);
//
//        return dto;
    }
    private EmployeeDTO mapToEmployeeDTO(User user) {
        return new EmployeeDTO(
                user.getUserId(),
                user.getFullName(),
                user.getUserRole()
        );
    }

    private ServiceRequestDTO mapToDTO(Booking booking, BookingAssignment bookingAssignment) {
        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setId(booking.getBookingId());
        dto.setUserId(booking.getUser().getUserId());
        dto.setServiceId(booking.getService().getServiceId());
        dto.setStatus(booking.getStatus().toString());
        dto.setAddress(booking.getLocation());
        dto.setOrganizationId(bookingAssignment.getOrganization().getId());
        dto.setOrganizationName(bookingAssignment.getOrganization().getName());
        dto.setAgreedPrice(bookingAssignment.getAgreedPrice());
        dto.setComments(booking.getDescription());
        dto.setRating(booking.getRating());
        dto.setFeedback(booking.getFeedback());
        dto.setDate(LocalDate.parse(booking.getDate()));
        dto.setTime(LocalTime.parse(booking.getTime()));
        dto.setPaymentStatus(booking.getPaymentStatus());
        dto.setFullName(booking.getUser().getFullName());
        dto.setRole(booking.getUser().getUserRole());

        List<EmployeeDTO> employeesInvolved = bookingAssignment.getAssignedEmployees().stream()
                .map(this::mapToEmployeeDTO)
                .collect(Collectors.toList());
        dto.setEmployeesInvolved(employeesInvolved);

        return dto;
    }
}
