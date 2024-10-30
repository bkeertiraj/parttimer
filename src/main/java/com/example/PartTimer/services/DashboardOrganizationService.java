package com.example.PartTimer.services;

import com.example.PartTimer.dto.dashboard.*;
import com.example.PartTimer.entities.*;
import com.example.PartTimer.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardOrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ServiceAssignmentRepository serviceAssignmentRepository;
    private final OrganizationServiceRepository organizationServiceRepository;

    public DashboardStatsDTO getDashboardStats(Long orgId) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        DashboardStatsDTO stats = DashboardStatsDTO.builder()
                .totalEmployees(userRepository.countByOrganizationId(orgId))
                .totalServices(organizationServiceRepository.countByOrganizationId(orgId))
                .totalBookings(bookingRepository.countByServiceAssignmentOrganizationId(orgId))
                .completedBookings(bookingRepository.countByServiceAssignmentOrganizationIdAndStatus(orgId, BookingStatus.COMPLETED))
                .pendingBookings(bookingRepository.countByServiceAssignmentOrganizationIdAndStatus(orgId, BookingStatus.PAYMENT_PENDING))
                .build();
        System.out.println("Dashboard Stats: " + stats);
        return stats;

    }

    public List<DashboardUserDTO> getEmployees(Long orgId) {
        List<DashboardUserDTO> employees = userRepository.findByOrganizationId(orgId).stream()
                .map(this::convertToDashboardUserDTO)
                .collect(Collectors.toList());

        System.out.println("Employees: " + employees);
        return employees;
    }

    // DTO conversion methods
    private DashboardUserDTO convertToDashboardUserDTO(User user) {
        return DashboardUserDTO.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userRole(user.getUserRole())
                .build();
    }

    public List<OrganizationServiceDTO> getOrganizationServices(Long orgId) {
        return organizationServiceRepository.findByOrganizationId(orgId).stream()
                .map(this::convertToOrgServiceDTO)
                .collect(Collectors.toList());
    }

    private OrganizationServiceDTO convertToOrgServiceDTO(OrganizationService orgService) {
        return OrganizationServiceDTO.builder()
                .id(orgService.getId())
                .serviceName(orgService.getService().getName())
                .expectedFee(orgService.getExpectedFee())
                .build();
    }

    public List<DashboardBookingDTO> getBookings(Long orgId, BookingStatus status) {
        List<Booking> bookings;
        if (status != null) {
            bookings = bookingRepository.findByServiceAssignmentOrganizationIdAndStatus(orgId, status);
        } else {
            bookings = bookingRepository.findByServiceAssignmentOrganizationId(orgId);
        }
        return bookings.stream()
                .map(this::convertToBookingDTO)
                .collect(Collectors.toList());
    }

    //latest booking details
    public List<DashboardBookingDTO> getLatestBookingsById(Long orgId) {
//        Pageable pageable = PageRequest.of(0, 10); // Increased to 10 to accommodate both types
//        List<Booking> bookings = bookingRepository.findLatestAndPostedBookings(orgId, pageable);
//
//        return bookings.stream()
//                .sorted((b1, b2) -> {
//                    // Sort POSTED status first, then by bookingId DESC
//                    if (b1.getStatus() == BookingStatus.POSTED && b2.getStatus() != BookingStatus.POSTED) {
//                        return -1;
//                    } else if (b1.getStatus() != BookingStatus.POSTED && b2.getStatus() == BookingStatus.POSTED) {
//                        return 1;
//                    }
//                    return b2.getBookingId().compareTo(b1.getBookingId());
//                })
//                .map(this::convertToBookingDTO)
//                .collect(Collectors.toList());

        List<Booking> newBookings = bookingRepository.findNewlyPostedBookings(orgId);

        return newBookings.stream()
                .map(this::convertToBookingDTO)
                .collect(Collectors.toList());
    }


    private DashboardBookingDTO convertToBookingDTO(Booking booking) {
        return DashboardBookingDTO.builder()
                .bookingId(booking.getBookingId())
                .serviceName(booking.getService().getName())
                .customerName(booking.getName())
                .date(booking.getDate())
                .time(booking.getTime())
                .status(booking.getStatus())
                .paymentStatus(booking.getPaymentStatus())
                .build();
    }

    public List<DashboardServiceAssignmentDTO> getServiceAssignments(Long orgId) {
        return serviceAssignmentRepository.findByOrganizationId(orgId).stream()
                .map(this::convertToServiceAssignmentDTO)
                .collect(Collectors.toList());
    }

    private DashboardServiceAssignmentDTO convertToServiceAssignmentDTO(ServiceAssignment serviceAssignment) {
        return DashboardServiceAssignmentDTO.builder()
                .id(serviceAssignment.getId())
                .bookingId(serviceAssignment.getBooking().getBookingId())
                .serviceName(serviceAssignment.getBooking().getService().getName())
                .agreedPrice(serviceAssignment.getAgreedPrice())
                .build();
    }

    public OrganizationServiceDTO updateServiceFee(Long orgId, Long serviceId, Double expectedFee) {
        OrganizationService orgService = organizationServiceRepository
                .findByOrganizationIdAndServiceId(orgId, serviceId)
                .orElseThrow(() -> new RuntimeException("Organization service not found"));

        orgService.setExpectedFee(expectedFee);
        return convertToOrgServiceDTO(organizationServiceRepository.save(orgService));
    }

    public DashboardUserDTO addEmployee(Long orgId, DashboardUserDTO employeeDTO) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        User employee = new User();
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setEmail(employeeDTO.getEmail());
        employee.setPhoneNumber(employeeDTO.getPhoneNumber());
        employee.setOrganization(org);
        employee.setUserRole(UserRole.EMPLOYEE);

        return convertToDashboardUserDTO(userRepository.save(employee));
    }
}
