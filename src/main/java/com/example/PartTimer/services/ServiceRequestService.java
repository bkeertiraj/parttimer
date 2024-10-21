package com.example.PartTimer.services;

import com.example.PartTimer.dto.*;
import com.example.PartTimer.entities.*;
import com.example.PartTimer.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServiceRequestService {

    private final BookingRepository bookingRepository;
    private final ServiceAssignmentRepository serviceAssignmentRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationServiceRepository organizationServiceRepository;
    private final UserRepository userRepository;

    @Autowired
    public ServiceRequestService(
            BookingRepository bookingRepository,
            ServiceAssignmentRepository serviceAssignmentRepository,
            OrganizationRepository organizationRepository, OrganizationServiceRepository organizationServiceRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.serviceAssignmentRepository = serviceAssignmentRepository;
        this.organizationRepository = organizationRepository;
        this.organizationServiceRepository = organizationServiceRepository;
        this.userRepository = userRepository;
    }

    public ServiceRequestDTO getServiceRequestDetails(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setId(booking.getBookingId());
        dto.setStatus(booking.getStatus().toString());
        dto.setAddress(booking.getLocation());
        dto.setDate(LocalDate.parse(booking.getDate()));
        dto.setTime(LocalTime.parse(booking.getTime()));
        dto.setPaymentStatus(booking.getPaymentStatus());
//
//        // If status is POSTED, show available organizations
//        if (booking.getStatus() == BookingStatus.POSTED) {
//            System.out.println("the condition is checked that booking status is POSTED");
//            // Fetch the available organizations for the service
//            List<OrganizationDTO> availableOrganizations = getAvailableOrganizationsForService(booking.getService().getServiceId());
//            // Set the list of available organizations in the DTO
//
//            dto.setAvailableOrganizations(availableOrganizations);
//        }
//
//        // If status is REQUEST_SENT or later, show organization details and employees
//        if (booking.getStatus().ordinal() >= BookingStatus.REQUEST_SENT.ordinal()) {
//            ServiceAssignment assignment = serviceAssignmentRepository.findByBooking(booking)
//                    .orElse(null);
//
//            if (assignment != null) {
//                Organization org = assignment.getOrganization();
//                dto.setOrganizationId(org.getId());
//                dto.setOrganizationName(org.getName());
//                dto.setAgreedPrice(assignment.getAgreedPrice());
//
//                // If status is CONFIRMED or later, add employees involved
//                if (booking.getStatus().ordinal() >= BookingStatus.CONFIRMED.ordinal()) {
//                    List<EmployeeDTO> employees = getServiceProviders(org);
//                    dto.setEmployeesInvolved(employees);
//                }
//            }
//        }
        //from github old version
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
            System.out.println("Available Organizations: " +availableOrganizations);
            dto.setAvailableOrganizations(availableOrganizations);


        }
        return dto;
    }

    private List<OrganizationDTO> getAvailableOrganizationsForService(Long serviceId) {
        List<OrganizationService> organizationServices = organizationServiceRepository.findOrganizationsByServiceId(serviceId);
        System.out.println("Fetched organizations for serviceId " + serviceId + ": " + organizationServices); // Add this to check if data is fetched
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

        if (booking.getStatus() != BookingStatus.POSTED) {
            throw new RuntimeException("Cannot select organization in current state");
        }

//        Organization organization = organizationRepository.findById(organizationId)
//                .orElseThrow(() -> new RuntimeException("Organization not found"));

        Organization organization = organizationRepository.findByIdWithOwnersAndCoOwners(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // Debug prints
        System.out.println("Organization ID: " + organization.getId());
        System.out.println("Organization Name: " + organization.getName());

        // Print owner details
        if (organization.getOwner() != null) {
            System.out.println("Owner ID: " + organization.getOwner().getUserId());
            System.out.println("Owner Name: " + organization.getOwner().getFullName());
        } else {
            System.out.println("No owner assigned");
        }

        // Print co-owner details
        if (organization.getCoOwners() != null && !organization.getCoOwners().isEmpty()) {
            System.out.println("Number of co-owners: " + organization.getCoOwners().size());
            organization.getCoOwners().forEach(coOwner -> {
                System.out.println("Co-owner ID: " + coOwner.getUserId());
                System.out.println("Co-owner Name: " + coOwner.getFullName());
            });
        } else {
            System.out.println("No co-owners found");
        }

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

        // Update booking status
        booking.setStatus(BookingStatus.REQUEST_SENT); //
        bookingRepository.save(booking);
        serviceAssignmentRepository.save(assignment);


        // Get service request details and add owner/co-owner information
        ServiceRequestDTO dto = getServiceRequestDetails(bookingId);

        List<EmployeeDTO> employeesInvolved = new ArrayList<>();

        User owner = userRepository.findOwnerByOrganizationId(organizationId);
//        if (owner != null) {
//            employeesInvolved.add(new EmployeeDTO(
//                    owner.getUserId(),
//                    owner.getFullName(),
//                    owner.getUserRole()
//            ));
//            System.out.println("Added owner: " + owner.getFullName());
//        }
//
//        // Add co-owners
//        List<User> coOwners = userRepository.findCoOwnersByOrganizationId(organizationId);
//        if (!coOwners.isEmpty()) {
//            coOwners.forEach(coOwner -> {
//                employeesInvolved.add(new EmployeeDTO(
//                        coOwner.getUserId(),
//                        coOwner.getFullName(),
//                        coOwner.getUserRole()
//                ));
//                System.out.println("Added co-owner: " + coOwner.getFullName());
//            });
//        }
//        //List<User> coOwners = userRepository.findCoOwnersByOrganizationId(organizationId);
//        System.out.println("SELECTED ORGANIZATION: " + organization.getName());
//        if (owner != null) {
//            System.out.println("OWNER OF THIS ORGANIZATION: " + owner.getFullName());
//        }
//        System.out.println("CO-OWNERS OF THIS ORGANIZATION: " +
//                coOwners.stream()
//                        .map(User::getFullName)
//                        .collect(Collectors.joining(", ")));
//
//        dto.setEmployeesInvolved(employeesInvolved);

        return dto;

        //return getServiceRequestDetails(bookingId);  // getServiceRequestDetails(bookingId);

//        if (booking.getStatus() != BookingStatus.POSTED) {
//            throw new RuntimeException("Cannot select organization in current state");
//        }

        // Create service assignment
//        ServiceAssignment assignment = new ServiceAssignment();
//        assignment.setBooking(booking);
//        assignment.setOrganization(organizationRepository.findById(organizationId)
//                .orElseThrow(() -> new RuntimeException("Organization not found")));
//
//        // Update booking status
//        booking.setStatus(BookingStatus.REQUEST_SENT);
//        booking = bookingRepository.save(booking);
//        serviceAssignmentRepository.save(assignment);
//
//        return mapToServiceRequestDTO(booking);
    }

    public ServiceRequestDTO simulatePayment(Long id, PaymentSimulationDTO paymentInfo) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.PAYMENT_PENDING) {
            booking.setStatus(BookingStatus.PAYMENT_PENDING); //PAYMENT_PENDING
            booking = bookingRepository.save(booking);
        }

        // Simulate payment processing delay
        try {
            Thread.sleep(2000); // 2 second delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Update payment status
//        booking.setPaymentStatus(PaymentStatus.COMPLETED);
//        booking.setStatus(BookingStatus.REQUEST_SENT);
//        bookingRepository.save(booking);
//        return getServiceRequestDetails(id);
        booking.setStatus(BookingStatus.PAYMENT_SUBMITTED);
        booking.setPaymentStatus(PaymentStatus.COMPLETED);
        booking = bookingRepository.save(booking);

        // After payment is completed, move to COMPLETED status
        booking.setStatus(BookingStatus.COMPLETED);
        booking = bookingRepository.save(booking);

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

        ServiceAssignment serviceAssignment = serviceAssignmentRepository.findByBooking(booking)
                .orElseThrow(() -> new RuntimeException("Service assignment not found for booking ID: " + booking.getBookingId()));

        // Get the assigned organization
        Organization organization = serviceAssignment.getOrganization();

        // For Owners
        List<String> ownerNames = new ArrayList<>();
        Owner owner = organization.getOwner(); // Ensure this returns a single Owner
        if (owner != null) {
            ownerNames.add(owner.getFirstName());
        }

        // For Co-Owners
        List<String> coOwnerNames = organization.getCoOwners().stream() // Assuming this returns a collection
                .map(CoOwner::getFirstName) // Assuming getFirstName() exists in CoOwner
                .collect(Collectors.toList());

        // Create and populate the DTO with owners and co-owners

        ServiceRequestDTO serviceRequestDTO = mapToServiceRequestDTO(booking);
        serviceRequestDTO.setOrganizationName(organization.getName());
//        serviceRequestDTO.setOwnerNames(ownerNames);
//        serviceRequestDTO.setCoOwnerNames(coOwnerNames);
//        serviceRequestDTO.setEmployeesInvolved(ownerNames);
//        serviceRequestDTO.setEmployeesInvolved(coOwnerNames);
        System.out.println("names of employees and organization: " + organization.getName() +  ownerNames +  coOwnerNames);

        // Return the updated DTO after mapping
        //send name of organization and employees
        return serviceRequestDTO; // Return the populated DTO instead of mapping it again

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

        if (booking.getStatus() != BookingStatus.REQUEST_SENT) {
            throw new RuntimeException("Cannot confirm request in current state");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking = bookingRepository.save(booking);

        ServiceRequestDTO dto = mapToServiceRequestDTO(booking);

        // Add service providers information //this section can be removed
//        ServiceAssignment assignment = serviceAssignmentRepository.findByBooking(booking)
//                .orElseThrow(() -> new RuntimeException("Service assignment not found"));
//
//        List<EmployeeDTO> serviceProviders = getServiceProviders(assignment.getOrganization());
//        dto.setEmployeesInvolved(serviceProviders);

        return dto;
    }

    public ServiceRequestDTO submitFeedback(Long id, FeedbackDTO feedback) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new RuntimeException("Cannot submit feedback before service completion");
        }
        // Set feedback details in the booking entity
        booking.setRating(feedback.getRating());
        booking.setFeedback(feedback.getFeedback());
        bookingRepository.save(booking);

        // Return the updated DTO
        return mapToServiceRequestDTO(booking); //can be id here
    }

    public ServiceRequestDTO confirmAndAssignEmployees(Long id, List<Long> assignedEmployeeIds) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));


        ServiceAssignment request = serviceAssignmentRepository.findByBooking(booking)
                .orElse(null);


        booking.setStatus(BookingStatus.CONFIRMED);
        List<User> assignedEmployees = userRepository.findAllById(assignedEmployeeIds);
        booking.setServiceProviders(new HashSet<>(assignedEmployees));

        booking = bookingRepository.save(booking);

        ServiceRequestDTO dto = mapToDTO(booking, request);
        dto.setEmployeesInvolved(assignedEmployees.stream()
                .map(this::mapToEmployeeDTO)
                .collect(Collectors.toList()));

        return dto;

    }

    private EmployeeDTO mapToEmployeeDTO(User user) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(user.getUserId());
        dto.setName(user.getFullName());
        // Set other fields as needed
        return dto;
    }

    private ServiceRequestDTO mapToDTO(Booking booking, ServiceAssignment request) {
        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setId(booking.getBookingId());
        dto.setUserId(booking.getUser().getUserId());
        dto.setServiceId(booking.getBookingId());
        dto.setStatus(booking.getStatus().toString());
        dto.setAddress(booking.getLocation());
        dto.setOrganizationId(request.getOrganization().getId());
        dto.setOrganizationName(request.getOrganization().getName());
        dto.setAgreedPrice(booking.getService().getBaseFee()); // Assuming there's a price field in Service
        dto.setComments(booking.getDescription());
        dto.setRating(booking.getRating());
        dto.setFeedback(booking.getFeedback());
        dto.setDate(LocalDate.parse(booking.getDate()));
        dto.setTime(LocalTime.parse(booking.getTime())); // Assuming time is stored as String
        dto.setPaymentStatus(booking.getPaymentStatus());
        dto.setFullName(booking.getUser().getFullName());
        dto.setRole(booking.getUser().getUserRole());

        // Set employeesInvolved
        List<EmployeeDTO> employeesInvolved = booking.getServiceProviders().stream()
                .map(this::mapToEmployeeDTO)
                .collect(Collectors.toList());
        dto.setEmployeesInvolved(employeesInvolved);

        return dto;
    }


//    private List<EmployeeDTO> getServiceProviders(Organization organization) {
//        List<EmployeeDTO> providers = new ArrayList<>();
//
//        // Add owner
//        Owner owner = organization.getOwner();
//        if (owner != null) {
//            providers.add(new EmployeeDTO(
//                    owner.getUserId(),
//                    owner.getFirstName() + " " + owner.getLastName(),
//                    "Owner"
//            ));
//        }
//
//        // Add co-owners
//        Set<CoOwner> coOwners = organization.getCoOwners();
//        for (CoOwner coOwner : coOwners) {
//            providers.add(new EmployeeDTO(
//                    coOwner.getUserId(),
//                    coOwner.getFirstName() + " " + coOwner.getLastName(),
//                    "Co-Owner"
//            ));
//        }
//
//        return providers;
//    }

}
