package com.example.PartTimer.services.labour;

import com.example.PartTimer.dto.labour.LabourAssignmentDetailsDTO;
import com.example.PartTimer.dto.labour.LabourBookingDTO;
import com.example.PartTimer.dto.labour.LabourBookingsByUserDTO;
import com.example.PartTimer.dto.labour.PriceOfferDetailsDTO;
import com.example.PartTimer.entities.User;
import com.example.PartTimer.entities.labour.*;
import com.example.PartTimer.repositories.UserRepository;
import com.example.PartTimer.repositories.labour.LabourAssignmentRepository;
import com.example.PartTimer.repositories.labour.LabourBookingRepository;
import com.example.PartTimer.repositories.labour.LabourPriceOfferCountRepository;
import com.example.PartTimer.repositories.labour.LabourPriceOfferRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Autowired
    private LabourPriceOfferRepository priceOfferRepository;

    @Autowired
    private LabourPriceOfferCountRepository labourPriceOfferCountRepository;

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
        booking.setCity(labourBookingDTO.getCity());
        booking.setZipcode(labourBookingDTO.getZipcode());
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

        List<LabourBooking> bookings = labourBookingRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
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
//                            long priceOfferCount = labourAssignmentRepository.countPriceOffersForAssignment(assignment.getId());
//                            assignmentDTO.setNumberOfPriceOffers((int) priceOfferCount);
                            int priceOfferCount = labourPriceOfferCountRepository.findByLabourBookingId(booking.getId())
                                    .map(LabourPriceOfferCount::getOfferCount)
                                    .orElseGet(() -> calculateAndSaveOfferCount(booking)); // Fallback for old data

                            assignmentDTO.setNumberOfPriceOffers(priceOfferCount);

                            return assignmentDTO;
                        })
                        .collect(Collectors.toList())
        );

        return dto;
    }

    private int calculateAndSaveOfferCount(LabourBooking booking) {
        // Dynamically calculate the count of price offers for the booking
        int count = priceOfferRepository.countByLabourAssignment_BookingId(booking.getId());

        // Save the calculated count into LabourPriceOfferCount for future use
        LabourPriceOfferCount offerCount = new LabourPriceOfferCount();
        offerCount.setLabourBooking(booking);
        offerCount.setOfferCount(count);
        labourPriceOfferCountRepository.save(offerCount);

        return count;
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

    public List<PriceOfferDetailsDTO> getPriceOffersForAssignment(Long labourAssignmentId) {
        LabourAssignment labourAssignment = labourAssignmentRepository.findById(labourAssignmentId)
                .orElseThrow(() -> new RuntimeException("Labour Assignment not found"));

        // Fetch all price offers for this assignment
//        List<LabourPriceOffer> priceOffers = priceOfferRepository.findByLabourAssignment(labourAssignment);

        // Fetch only PENDING price offers for this assignment
        List<LabourPriceOffer> priceOffers = priceOfferRepository
                .findByLabourAssignmentAndStatus(labourAssignment, LabourPriceOfferStatus.PENDING);


        // Convert to DTOs
        return priceOffers.stream()
                .map(this::convertToPriceOfferDTO)
                .collect(Collectors.toList());
    }

    private PriceOfferDetailsDTO convertToPriceOfferDTO(LabourPriceOffer priceOffer) {
        PriceOfferDetailsDTO dto = new PriceOfferDetailsDTO();

        // Price offer details
        dto.setPriceOfferId(priceOffer.getId());
        dto.setProposedPrice(priceOffer.getOfferedPrice());

        // Labour details
        Labour labour = priceOffer.getLabour();
        dto.setLabourFirstName(labour.getFirstName());
        dto.setLabourMiddleName(labour.getMiddleName());
        dto.setLabourLastName(labour.getLastName());

        // Calculate and set labour rating
        dto.setLabourRating(labour.getAverageRating());

        return dto;
    }

    public LabourAssignmentDetailsDTO getLabourAssignmentDetails(Long labourAssignmentId) {
        LabourAssignment labourAssignment = labourAssignmentRepository.findById(labourAssignmentId)
                .orElseThrow(() -> new RuntimeException("Labour Assignment not found"));

        LabourAssignmentDetailsDTO dto = new LabourAssignmentDetailsDTO();
        dto.setDate(labourAssignment.getBookingDate());
        dto.setTimeSlot(labourAssignment.getTimeSlot());
        dto.setStatus(labourAssignment.getBookingStatus().name());
        dto.setDescription(labourAssignment.getBookingNote());

        LabourBooking booking = labourAssignment.getBooking();
        dto.setLocation(booking.getAddress());
        dto.setZipcode(booking.getZipcode());
        dto.setCity(booking.getCity());

        return dto;
    }

    @Transactional
    public void acceptPriceOffer(Long priceOfferId) {
        LabourPriceOffer selectedPriceOffer = priceOfferRepository.findById(priceOfferId)
                .orElseThrow(() -> new RuntimeException("Price Offer not found"));

        LabourAssignment selectedAssignment = selectedPriceOffer.getLabourAssignment();
        Labour selectedLabour = selectedPriceOffer.getLabour();
        LocalDate bookingDate = selectedAssignment.getBookingDate();

        // Find all PENDING price offers for this labour on the same date
        List<LabourPriceOffer> labourPriceOffersOnDate = priceOfferRepository
                .findByLabourAndLabourAssignmentBookingDateAndStatus(
                        selectedLabour,
                        bookingDate,
                        LabourPriceOfferStatus.PENDING
                );


        // Withdraw conflicting price offers
        labourPriceOffersOnDate.forEach(priceOffer -> {
            if (!priceOffer.getId().equals(selectedPriceOffer.getId())) {

                if (isTimeSlotConflicting(
                        selectedAssignment.getTimeSlot(),
                        priceOffer.getLabourAssignment().getTimeSlot()
                )) {
                    priceOffer.setStatus(LabourPriceOfferStatus.WITHDRAWN);
                    priceOfferRepository.save(priceOffer);
                }
            }
        });

        // Withdraw all pending price offers for the same booking
        List<LabourPriceOffer> allPendingOffersForBooking = priceOfferRepository
                .findByLabourAssignmentBookingAndStatus(
                        selectedAssignment.getBooking(),
                        LabourPriceOfferStatus.PENDING
                );

        allPendingOffersForBooking.forEach(offer -> {
            offer.setStatus(LabourPriceOfferStatus.WITHDRAWN);
            priceOfferRepository.save(offer);
        });

        // Set the selected price offer to ACCEPTED
        selectedPriceOffer.setStatus(LabourPriceOfferStatus.ACCEPTED);
        priceOfferRepository.save(selectedPriceOffer);

        // Update the corresponding labour assignment status
        selectedAssignment.setBookingStatus(LabourBookingStatus.ACCEPTED);
        labourAssignmentRepository.save(selectedAssignment);
    }

    private boolean isTimeSlotConflicting(String selectedTimeSlot, String existingTimeSlot) {
        // If "Full Day" is selected or exists, it conflicts with any other slot
        if (selectedTimeSlot.equals("Full Day") || existingTimeSlot.equals("Full Day")) {
            return true;
        }

        // "AM" slot conflicts only with "Full Day"
        if (selectedTimeSlot.equals("7:30 AM - 11:30 AM") && existingTimeSlot.equals("Full Day")) {
            return true;
        }
        if (selectedTimeSlot.equals("12:30 PM - 4:30 PM") && existingTimeSlot.equals("7:30 AM - 11:30 AM")) {
            return false;
        }

        // "PM" slot conflicts only with "Full Day"
        if (selectedTimeSlot.equals("12:30 PM - 4:30 PM")) {
            return existingTimeSlot.equals("Full Day") ;
        }

        return false;

    }

}
