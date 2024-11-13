package com.example.PartTimer.services;

import com.example.PartTimer.dto.BookingAssignmentDTO;
import com.example.PartTimer.entities.*;
import com.example.PartTimer.repositories.*;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingAssignmentService {

    private final BookingAssignmentRepository bookingAssignmentRepository;
    private final ServiceAssignmentRepository serviceAssignmentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final OrganizationRepository organizationRepository;

    public BookingAssignmentService(BookingAssignmentRepository bookingAssignmentRepository, ServiceAssignmentRepository serviceAssignmentRepository, UserRepository userRepository, BookingRepository bookingRepository, OrganizationRepository organizationRepository) {
        this.bookingAssignmentRepository = bookingAssignmentRepository;
        this.serviceAssignmentRepository = serviceAssignmentRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.organizationRepository = organizationRepository;
    }

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
        ServiceAssignment assignment = new ServiceAssignment();
        assignment.setBooking(booking);
        assignment.setOrganization(organization);

        BookingAssignment bookingAssignment = new BookingAssignment();
        bookingAssignment.setBooking(booking);
        bookingAssignment.setOrganization(assignment.getOrganization());

        bookingAssignment.setAssignedEmployees(new HashSet<>(employees));
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

        return dto;
    }

}
