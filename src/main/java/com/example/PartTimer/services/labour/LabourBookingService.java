package com.example.PartTimer.services.labour;

import com.example.PartTimer.dto.labour.LabourBookingDTO;
import com.example.PartTimer.dto.labour.LabourBookingsByUserDTO;
import com.example.PartTimer.entities.User;
import com.example.PartTimer.entities.labour.LabourAssignment;
import com.example.PartTimer.entities.labour.LabourBooking;
import com.example.PartTimer.entities.labour.LabourBookingStatus;
import com.example.PartTimer.repositories.UserRepository;
import com.example.PartTimer.repositories.labour.LabourAssignmentRepository;
import com.example.PartTimer.repositories.labour.LabourBookingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LabourBookingService {

    @Autowired
    private LabourBookingRepository labourBookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabourAssignmentRepository labourAssignmentRepository;

    @Transactional
    public LabourBooking createLabourBooking(LabourBookingDTO labourBookingDTO) {

        //get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        System.out.println("Current authenticated user's email in Labour Booking method: " + currentUserEmail);
        String userEmail = null;
        try {
            userEmail = authentication.getName();
            System.out.println("Authentication name: " + userEmail);

            if (userEmail == null || "anonymousUser".equals(userEmail)) {
                System.err.println("User is not authenticated");
                throw new IllegalStateException("User is not authenticated");
            }
        } catch (Exception e) {
            System.err.println("Error extracting authentication: " + e.getMessage());
            throw new IllegalStateException("Could not extract user authentication", e);
        }

        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        User currentUser = userOptional.get();
        System.out.println("User email from checking the email: " + currentUser .getEmail());

        LabourBooking booking = new LabourBooking();
        booking.setAddress(labourBookingDTO.getAddress());
        booking.setPhoneNumber(labourBookingDTO.getPhoneNumber());
        booking.setEmail(labourBookingDTO.getEmail());
        booking.setUser(currentUser);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        //create LabourAssignments
        List<LabourAssignment> assignments = labourBookingDTO.getLaborDetails().stream()
                .map(detail -> {
                    LabourAssignment assignment = new LabourAssignment();
                    assignment.setBooking(booking);
                    assignment.setBookingDate(detail.getDate().toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate());
                    assignment.setTimeSlot(detail.getTimeSlot());
                    assignment.setBookingNote(detail.getNote());
                    assignment.setBookingStatus(LabourBookingStatus.OPEN);
                    return assignment;

                })
                .collect(Collectors.toList());

        booking.setLabourAssignments(assignments);
        //save the booking
        return labourBookingRepository.save(booking);

    }

    public List<LabourBookingsByUserDTO> getLabourBookingsByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        List<LabourBooking> bookings = labourBookingRepository.findByUserEmail(userEmail);
        return bookings.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private LabourBookingsByUserDTO convertToDTO(LabourBooking booking) {
        LabourBookingsByUserDTO dto = new LabourBookingsByUserDTO();
        dto.setBookingId(booking.getId());
//        dto.setAddress(booking.getAddress());
//        dto.setPhoneNumber(booking.getPhoneNumber());
//        dto.setEmail(booking.getEmail());
//        dto.setCreatedAt(booking.getCreatedAt());
//        dto.setUpdatedAt(booking.getUpdatedAt());

        // Convert labour assignments
//        dto.setLabourAssignments(
//                booking.getLabourAssignments().stream().map(this::convertAssignmentToDTO)
//                        .collect(Collectors.toList())
//        );
        dto.setLabourAssignments(
                booking.getLabourAssignments().stream()
                        .map(assignment -> {
                            LabourBookingsByUserDTO.LabourAssignmentDTO assignmentDTO = convertAssignmentToDTO(assignment);

                            // Count price offers for this specific labour assignment
                            long priceOfferCount = labourAssignmentRepository.countPriceOffersForAssignment(assignment.getId());
                            assignmentDTO.setNumberOfPriceOffers((int) priceOfferCount);

                            return assignmentDTO;
                        })
                        .collect(Collectors.toList())
        );

        return dto;
    }

    private LabourBookingsByUserDTO.LabourAssignmentDTO convertAssignmentToDTO(LabourAssignment assignment) {
        LabourBookingsByUserDTO.LabourAssignmentDTO dto = new LabourBookingsByUserDTO.LabourAssignmentDTO();
        dto.setAssignmentId(assignment.getId());
//        if (assignment.getLabour() != null) {
//            dto.setLabourId(assignment.getLabour().getId());
//        } else {
//            dto.setLabourId(null); // Explicitly set to null
//        }
        dto.setBookingDate(assignment.getBookingDate());
        dto.setTimeSlot(assignment.getTimeSlot());
        dto.setBookingNote(assignment.getBookingNote());
        dto.setBookingStatus(assignment.getBookingStatus().name());
//        dto.setProposedPrice(assignment.getProposedPrice());
        return dto;
    }
}
