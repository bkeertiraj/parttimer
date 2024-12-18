package com.example.PartTimer.services.labour;

import com.example.PartTimer.dto.labour.LabourBookingsByUserDTO;
import com.example.PartTimer.dto.labour.LabourPriceOfferDTO;
import com.example.PartTimer.dto.labour.OpenBookingsForLabourDashboardDTO;
import com.example.PartTimer.entities.labour.*;
import com.example.PartTimer.repositories.labour.LabourAssignmentRepository;
import com.example.PartTimer.repositories.labour.LabourBookingRepository;
import com.example.PartTimer.repositories.labour.LabourPriceOfferRepository;
import com.example.PartTimer.repositories.labour.LabourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabourDashboardService {

    @Autowired
    private LabourAssignmentRepository labourAssignmentRepository;

    @Autowired
    private LabourRepository labourRepository;

    @Autowired
    private LabourBookingRepository labourBookingRepository;

    @Autowired
    private LabourPriceOfferRepository priceOfferRepository;

    // Method to get current labour's ID by phone number from authentication
    public Long getCurrentLabourId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Assuming the username in the authentication is the phone number
        String phoneNumber = authentication.getName();

        return labourRepository.findByPhoneNumber(phoneNumber)
                .map(Labour::getId)
                .orElseThrow(() -> new RuntimeException("Labour not found with phone number: " + phoneNumber));
    }

    public List<OpenBookingsForLabourDashboardDTO> getOpenBookings() {
        List<LabourAssignment> openAssignments = labourAssignmentRepository.findOpenBookings();

        return openAssignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private OpenBookingsForLabourDashboardDTO convertToDTO(LabourAssignment assignment) {
        OpenBookingsForLabourDashboardDTO dto = new OpenBookingsForLabourDashboardDTO();
        dto.setId(assignment.getId());
        dto.setBookingDate(assignment.getBookingDate());
        dto.setTimeSlot(assignment.getTimeSlot());
        dto.setBookingNote(assignment.getBookingNote());
        dto.setBookingStatus(assignment.getBookingStatus());
        dto.setBookingId(assignment.getBooking().getId());

        dto.setCity("Podapadar");
        dto.setZipcode("761026");
        return dto;
    }

    public LabourPriceOffer offerPriceForBooking(LabourPriceOfferDTO priceOfferDTO) {
        // Fetch the specific labour assignment
        LabourAssignment labourAssignment = labourAssignmentRepository.findById(priceOfferDTO.getLabourAssignmentId())
                .orElseThrow(() -> new RuntimeException("Labour Assignment not found"));

        // Get the current authenticated labour's ID
        Long currentLabourId = getCurrentLabourId();
        Labour currentLabour = labourRepository.findById(currentLabourId)
                .orElseThrow(() -> new RuntimeException("Labour not found"));

        // Check if labour has already made an offer for this booking
        boolean hasExistingOffer = priceOfferRepository.existsByLabourAssignmentAndLabour(labourAssignment, currentLabour);
        if (hasExistingOffer) {
            throw new IllegalStateException("You have already made an offer for this assignment");
        }

        // Create a new PriceOffer
        LabourPriceOffer priceOffer = new LabourPriceOffer();
        priceOffer.setLabourAssignment(labourAssignment);
        priceOffer.setLabour(currentLabour);
        priceOffer.setOfferedPrice(priceOfferDTO.getProposedPrice());
        priceOffer.setCreatedAt(LocalDateTime.now());

        return priceOfferRepository.save(priceOffer);
    }
}
