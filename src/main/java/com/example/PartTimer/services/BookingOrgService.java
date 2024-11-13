package com.example.PartTimer.services;

import com.example.PartTimer.dto.BookingDetailsDTO;
import com.example.PartTimer.dto.BookingStatusDTO;
import com.example.PartTimer.entities.Booking;
import com.example.PartTimer.entities.BookingAssignment;
import com.example.PartTimer.entities.BookingStatus;
import com.example.PartTimer.repositories.BookingAssignmentRepository;
import com.example.PartTimer.repositories.BookingRepository;
import com.example.PartTimer.repositories.OrganizationRepository;
import com.example.PartTimer.repositories.ServiceAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class BookingOrgService {

    private final BookingRepository bookingRepository;
    private final BookingAssignmentRepository bookingAssignmentRepository;
    private final ServiceAssignmentRepository serviceAssignmentRepository;
    private final OrganizationRepository organizationRepository;

    public BookingDetailsDTO getBookingDetails(Long orgId, Long bookingId) throws AccessDeniedException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check if organization has access to this booking
        //validateOrganizationAccess(orgId, booking);

        BookingAssignment assignment = bookingAssignmentRepository
                .findByBooking_BookingIdAndOrganization_Id(bookingId, orgId)
                .orElse(null);

        return mapToBookingDetailsDTO(booking, assignment);
    }

    private void validateOrganizationAccess(Long orgId, Booking booking) throws AccessDeniedException {
        boolean hasAccess = booking.getServiceProviders().stream()
                .anyMatch(org -> org.getOrganization().getId().equals(orgId));

        if(!hasAccess) {
            throw new AccessDeniedException("Organization does not have access to this booking");
        }
    }

    private BookingDetailsDTO mapToBookingDetailsDTO(Booking booking, BookingAssignment assignment) {
        BookingDetailsDTO dto = new BookingDetailsDTO();
        dto.setId(booking.getBookingId().toString());
        dto.setName(booking.getService().getName());
        dto.setStatus(booking.getStatus().toFrontendStatus());
        dto.setDescription(booking.getDescription());
        dto.setDate(booking.getDate());
        dto.setTime(booking.getTime());
        dto.setLocation(booking.getLocation());
        dto.setAddress(booking.getAddress());

        // Only include full address if status is confirmed or later
        if (booking.getStatus().ordinal() >= BookingStatus.SELLER_ACCEPTED.ordinal()) {
            dto.setAddress(booking.getAddress());
        }
        dto.setCity(booking.getAddress());
        dto.setZip(booking.getAddress());

        // Include email only if status is confirmed or later
        if (booking.getStatus().ordinal() >= BookingStatus.SELLER_ACCEPTED.ordinal()) {
            dto.setClientEmail(booking.getEmail());
        }

        if (assignment != null) {
            dto.setAssignedEmployees(assignment.getAssignedEmployees());
            dto.setAgreedPrice(assignment.getAgreedPrice());
        }

        return dto;
    }

    public BookingStatusDTO updateBookingStatus(Long orgId, Long bookingId, String newStatus) throws AccessDeniedException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        validateOrganizationAccess(orgId, booking);
        //validateStatusTransition
        booking.setStatus(BookingStatus.fromFrontendStatus(newStatus));
        booking = bookingRepository.save(booking);

        return new BookingStatusDTO(booking.getStatus().toFrontendStatus());
    }

}
