package com.example.PartTimer.services;

import com.example.PartTimer.dto.ServiceManagementDTO;
import com.example.PartTimer.entities.Booking;
import com.example.PartTimer.entities.BookingAssignment;
import com.example.PartTimer.entities.BookingStatus;
import com.example.PartTimer.entities.OrganizationService;
import com.example.PartTimer.repositories.BookingAssignmentRepository;
import com.example.PartTimer.repositories.BookingRepository;
import com.example.PartTimer.repositories.OrganizationServiceRepository;
import com.example.PartTimer.repositories.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceManagementService {

    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;
    private final OrganizationServiceRepository organizationServiceRepository;
    private final BookingAssignmentRepository bookingAssignmentRepository;

    public List<ServiceManagementDTO> getServicesForOrganization(Long organizationId) {
        // Get all services offered by the organization
        List<OrganizationService> orgServices = organizationServiceRepository
                .findByOrganizationId(organizationId);

        return orgServices.stream()
                .map(orgService -> {
                    ServiceManagementDTO dto = new ServiceManagementDTO();
                    com.example.PartTimer.entities.Service service = orgService.getService();

                    // Set basic service information
                    dto.setId(service.getServiceId());
                    dto.setName(service.getName());
                    dto.setCategory(service.getCategory());
                    dto.setSubcategory(service.getSubcategory());

                    // Get all bookings for this service in this organization
                    List<Booking> bookings = bookingRepository
                            .findByServiceAndOrganization(service.getServiceId(), organizationId);

                    // Calculate counts
                    dto.setPendingCount(countBookingsByStatus(bookings,
                            BookingStatus.OPEN, BookingStatus.SELLER_SELECTED));
                    dto.setOngoingCount(countBookingsByStatus(bookings,
                            BookingStatus.SELLER_ACCEPTED, BookingStatus.INITIATED, BookingStatus.PAYMENT_PENDING));
                    dto.setCompletedCount(countBookingsByStatus(bookings,
                            BookingStatus.COMPLETED));

                    // Calculate revenue
                    dto.setRevenue(calculateRevenue(bookings));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    private int countBookingsByStatus(List<Booking> bookings, BookingStatus... statuses) {
        return (int) bookings.stream()
                .filter(booking -> {
                    for (BookingStatus status : statuses) {
                        if (booking.getStatus() == status) {
                            return true;
                        }
                    }
                    return false;
                })
                .count();
    }

    private double calculateRevenue(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.COMPLETED)
                .mapToDouble(booking -> {
                    // Get the associated BookingAssignment to get the agreed price
                    BookingAssignment assignment = booking.getBookingAssignment();
                    if (assignment == null) {
                        assignment = bookingAssignmentRepository
                                .findByBookingId(booking.getBookingId())
                                .orElse(null);
                    }

                    return assignment != null ? assignment.getAgreedPrice() : 0.0;
                })
                .sum();
    }
}
