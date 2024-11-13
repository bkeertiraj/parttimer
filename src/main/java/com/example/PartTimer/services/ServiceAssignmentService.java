package com.example.PartTimer.services;

import com.example.PartTimer.dto.*;
import com.example.PartTimer.entities.*;
import com.example.PartTimer.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceAssignmentService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private ServiceRepository serviceRepository;

//    @Transactional
//    public EmployeeDTO assignServiceToEmployee(Long ownerId, Long employeeId, Long serviceId) {
//        Owner owner = ownerRepository.findById(ownerId)
//                .orElseThrow(() -> new RuntimeException("Owner not found"));
//        Employee employee = employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new RuntimeException("Employee not found"));
//        Service service = serviceRepository.findById(serviceId)
//                .orElseThrow(() -> new RuntimeException("Service not found"));
//
//        if (!owner.getEmployees().contains(employee)) {
//            throw new RuntimeException("This owner is not associated with the employee");
//        }
//
//        employee.getServices().add(service);
//        employeeRepository.save(employee);
//
//        return convertToEmployeeDTO(employee);
//    }

//    private EmployeeDTO convertToEmployeeDTO(Employee employee) {
//        EmployeeDTO dto = new EmployeeDTO();
//        dto.setEmployeeId(employee.getEmployeeId());
//        dto.setName(employee.getName());
//        dto.setEmail(employee.getEmail());
//        dto.setDesignation(employee.getDesignation());
//        dto.setStatus(employee.getStatus());
//        dto.setIsAdmin(employee.getIsAdmin());
//        dto.setRoleType(employee.getRoleType());
//
//        // If employee has owners, map them
//        if (employee.getOwners() != null && !employee.getOwners().isEmpty()) {
//            dto.setOwners(employee.getOwners().stream()
//                    .map(this::convertToOwnerDTO)  // Assuming you have a similar convertToOwnerDTO method
//                    .collect(Collectors.toList()));
//        }
//        dto.setServices(employee.getServices().stream()
//                .map(this::convertToServiceDTO)
//                .collect(Collectors.toList()));
//
//        return dto;
//    }
//
//    private ServiceDTO convertToServiceDTO(Service service) {
//        ServiceDTO dto = new ServiceDTO();
//        dto.setServiceId(service.getServiceId());
//        dto.setName(service.getName());
//        dto.setDescription(service.getDescription());
//        return dto;
//    }
//
//    private OwnerDTO convertToOwnerDTO(Owner owner) {
//
//        OwnerDTO dto = new OwnerDTO();
//        dto.setEmployeeId(owner.getEmployeeId());
//        dto.setName(owner.getName());
//        dto.setEmail(owner.getEmail());
//        dto.setDesignation(owner.getDesignation());
//        dto.setStatus(owner.getStatus());
//        dto.setPhoneNumber(owner.getPhoneNumber());
//        dto.setPassword(owner.getPassword());
//        return dto;
//    }
//    @Transactional
//    public List<EmployeeDTO> getEmployeesByService(Long serviceId) {
//        List<Employee> employees = employeeRepository.findEmployeesByServiceId(serviceId);
//        return convertToEmployeeDTOs(employees);
//    }
//
//    private List<EmployeeDTO> convertToEmployeeDTOs(List<Employee> employees) {
//        return employees.stream()
//                .map(this::convertToEmployeeDTO)
//                .collect(Collectors.toList());
//    }

    //03-11-2024
    @Autowired
    private ServiceAssignmentRepository serviceAssignmentRepository;

    private final BookingRepository bookingRepository;

    private final OrganizationRepository organizationRepository;

    public ServiceAssignmentService(BookingRepository bookingRepository, OrganizationRepository organizationRepository, UserRepository userRepository, BookingAssignmentRepository bookingAssignmentRepository) {
        this.bookingRepository = bookingRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.bookingAssignmentRepository = bookingAssignmentRepository;
    }

    public ServiceAssignmentDTO createOrUpdatePriceOffer(Long orgId, Long bookingId, Double offeredPrice) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        ServiceAssignment serviceAssignment = serviceAssignmentRepository
                .findByBooking_BookingIdAndOrganization_Id(bookingId, orgId)
                .orElse(new ServiceAssignment());

        // If existing assignment, store current price as previous price
        if(serviceAssignment.getId() != null) {
            System.out.println("agreed price value from the service assignment table: " + serviceAssignment.getAgreedPrice());
            serviceAssignment.setPrevPrice(serviceAssignment.getAgreedPrice());
        }

        serviceAssignment.setBooking(booking);
        serviceAssignment.setOrganization(organization);
        serviceAssignment.setAgreedPrice(offeredPrice);

        serviceAssignment = serviceAssignmentRepository.save(serviceAssignment);

        // Update booking status to REQUEST_SENT
        booking.setStatus(BookingStatus.SELLER_SELECTED);
        bookingRepository.save(booking);

        return mapToServiceAssignmentDTO(serviceAssignment);
    }

    public ServiceAssignmentDTO mapToServiceAssignmentDTO(ServiceAssignment serviceAssignment) {
        ServiceAssignmentDTO dto = new ServiceAssignmentDTO();
        dto.setBookingId(serviceAssignment.getBooking().getBookingId());
        dto.setOrganizationId(serviceAssignment.getOrganization().getId());
        dto.setAgreedPrice(serviceAssignment.getAgreedPrice());
        dto.setPrevPrice(serviceAssignment.getPrevPrice());

        dto.setServiceName(serviceAssignment.getBooking().getService().getName());
        dto.setDescription(serviceAssignment.getBooking().getDescription());

        // Add booking status and payment status from the Booking entity
        dto.setBookingStatus(serviceAssignment.getBooking().getStatus().name());
        dto.setPaymentStatus(serviceAssignment.getBooking().getPaymentStatus().name());

        // Set date, time, address, and location
        dto.setDate(serviceAssignment.getBooking().getDate().toString()); // Format date as needed
        dto.setTime(serviceAssignment.getBooking().getTime().toString());
        dto.setAddress(serviceAssignment.getBooking().getAddress());
        dto.setLocation(serviceAssignment.getBooking().getLocation());
        dto.setZip(serviceAssignment.getBooking().getAddress());

        return dto;
    }

    private final UserRepository userRepository;
    private final BookingAssignmentRepository bookingAssignmentRepository;

    public BookingAssignmentDTO assignEmployees(Long orgId, Long bookingId, List<Long> employeeIds) {
//        BookingAssignment assignment = bookingAssignmentRepository.findByBooking_BookingIdAndOrganization_Id(bookingId, orgId)
//                .orElseThrow(() -> new RuntimeException("Booking assignment not found"));
//
//        Set<User> employees = userRepository.findAllById(employeeIds)
//                .stream()
//                .collect(Collectors.toSet());
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        booking.setStatus(BookingStatus.SELLER_ACCEPTED);
        List<User> employees = userRepository.findAllById(employeeIds);

        // Validate all employees belong to the organization
        validateEmployeesOrganization(employees, orgId);

        // Create service assignment
//        ServiceAssignment assignment = new ServiceAssignment();
//        assignment.setBooking(booking);
//        assignment.setOrganization(organization);

        // Retrieve or create a ServiceAssignment
        ServiceAssignment serviceAssignment = serviceAssignmentRepository
                .findByBooking_BookingIdAndOrganization_Id(bookingId, orgId)
                .orElseGet(() -> {
                    ServiceAssignment newAssignment = new ServiceAssignment();
                    newAssignment.setBooking(booking);
                    newAssignment.setOrganization(organization);
                    newAssignment.setPrevPrice(0.0); // Default price if none set
                    return serviceAssignmentRepository.save(newAssignment);
                });


        BookingAssignment bookingAssignment = new BookingAssignment();
        bookingAssignment.setBooking(booking);
        bookingAssignment.setOrganization(serviceAssignment.getOrganization());

        // Set agreed price from serviceAssignment's prevPrice
        Double agreedPrice = serviceAssignment.getAgreedPrice();
        if (agreedPrice == null) {
            System.out.println("agreed price from service assignment for booking id: " + bookingId + "is coming null");
            agreedPrice = 0.0; // Ensure no null values
        }

        bookingAssignment.setAgreedPrice(agreedPrice); // Set agreedPrice on bookingAssignment

        bookingAssignment.setAssignedEmployees(new HashSet<>(employees));
        System.out.println("assigned employees, next is saving");
        bookingAssignment = bookingAssignmentRepository.save(bookingAssignment);
        return mapToBookingAssignmentDTO(bookingAssignment);
    }

    private void validateEmployeesOrganization(List<User> employees, Long orgId) {
        boolean allBelongToOrg = employees.stream()
                .allMatch(emp -> emp.getOrganization().getId().equals(orgId));

        if(!allBelongToOrg) {
            throw new RuntimeException("All employees must belong to the organization");
        }
    }
    public BookingAssignmentDTO mapToBookingAssignmentDTO(BookingAssignment assignment) {
        BookingAssignmentDTO dto = new BookingAssignmentDTO();
        dto.setBookingId(assignment.getBooking().getBookingId());
        dto.setOrganizationId(assignment.getOrganization().getId());

        // Map assigned employees to their IDs
        List<Long> employeeIds = assignment.getAssignedEmployees().stream()
                .map(User::getUserId)
                .collect(Collectors.toList());
        dto.setAssignedEmployeeIds(employeeIds);


        Booking booking = assignment.getBooking();

        dto.setStatus(booking.getStatus().toString());
        dto.setPaymentStatus(booking.getPaymentStatus() != null ? booking.getPaymentStatus().toString() : "PENDING");
        dto.setServiceName(booking.getService().getName());
        dto.setDescription(booking.getDescription());
        dto.setFeedback(booking.getUserFeedback());
        dto.setRating(booking.getUserRating());
        dto.setDate(LocalDate.parse(booking.getDate()));
        dto.setTime(LocalTime.parse(booking.getTime()));
        dto.setLocation(booking.getLocation());
        dto.setAddress(booking.getAddress());
        dto.setZip(booking.getAddress());
        dto.setClientEmail(booking.getEmail());

        ServiceAssignment serviceAssignment = serviceAssignmentRepository
                .findByBooking_BookingIdAndOrganization_Id(
                        assignment.getBooking().getBookingId(),
                        assignment.getOrganization().getId()
                )
                .orElse(null);

        if (serviceAssignment != null) {
            dto.setAgreedPrice(serviceAssignment.getAgreedPrice());
        }

        if(booking.getStatus() == BookingStatus.SELLER_ACCEPTED) {
            dto.setAddress(booking.getAddress());
        }

        return dto;
    }

    public BookingAssignmentDTO submitFeedback(Long bookingId, OrgFeedbackDTO feedback) {
        // Fetch the booking assignment by booking ID
        BookingAssignment bookingAssignment = bookingAssignmentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking assignment not found"));

        // Assuming `Booking` has a field to hold feedback data
        Booking booking = bookingAssignment.getBooking();
        booking.setUserRating(feedback.getUserRating());
        booking.setUserFeedback(feedback.getUserFeedback());
        // Save the feedback in booking entity
        bookingRepository.save(booking);

        // Map to BookingAssignmentDTO and return
        return mapToBookingAssignmentDTO(bookingAssignment);
    }

    public BookingAssignmentDTO updateStatus(Long bookingId, String status) {
        // Fetch the booking assignment by booking ID
        BookingAssignment bookingAssignment = bookingAssignmentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking assignment not found"));

        // Update the status in the booking
        Booking booking = bookingAssignment.getBooking();
        BookingStatus newStatus = BookingStatus.valueOf(status);

//        if(status.equals("PAYMENT_PENDING")) {
//            System.out.println("status now: "+status);
//            booking.setPaymentStatus(PaymentStatus.COMPLETED);
//        }
        booking.setStatus(BookingStatus.valueOf((status)));
        System.out.println("current status: "+status);

        // Save the updated booking status
        bookingRepository.save(booking);

        // Map the updated booking assignment to DTO and return
        return mapToBookingAssignmentDTO(bookingAssignment);
    }

    public OrganizationEmployeeDTO getOrganizationEmployeeDetails(Long orgId, Long bookingId) {
        var organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        List<OrganizationEmployeeDTO.EmployeeDetails> employeeDetails = organization.getMembers().stream()
                .map(user -> {
                    OrganizationEmployeeDTO.EmployeeDetails details = new OrganizationEmployeeDTO.EmployeeDetails();
                    details.setUserId(user.getUserId());
                    details.setFullName(user.getFullName());
                    details.setRole(user.getUserRole().name());
                    return details;
                })
                .collect(Collectors.toList());

        OrganizationEmployeeDTO dto = new OrganizationEmployeeDTO();
//        dto.setBookingId(booking.getBookingId());
//        dto.setOrganizationId(orgId);
//        dto.setBookingStatus(booking.getStatus().name());
//        dto.setLocation(booking.getLocation());
//        dto.setServiceName(booking.getService().getName());
//        dto.setDescription(booking.getDescription());
//        dto.setDate(booking.getDate());
//        dto.setTime(booking.getTime());
        dto.setEmployees(employeeDetails);

        return dto;
    }

}
