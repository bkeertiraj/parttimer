package com.example.PartTimer.services.labour;

import com.example.PartTimer.dto.labour.LabourBookingsByUserDTO;
import com.example.PartTimer.dto.labour.LabourPriceOfferDTO;
import com.example.PartTimer.dto.labour.OpenBookingsForLabourDashboardDTO;
import com.example.PartTimer.entities.labour.*;
import com.example.PartTimer.repositories.labour.*;
import jakarta.transaction.Transactional;
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

    @Autowired
    private LabourPriceOfferCountRepository labourPriceOfferCountRepository;

    // Method to get current labour's ID by phone number from authentication
    public Long getCurrentLabourId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Assuming the username in the authentication is the phone number
        String phoneNumber = authentication.getName();

        return labourRepository.findByPhoneNumber(phoneNumber)
                .map(Labour::getId)
                .orElseThrow(() -> new RuntimeException("Labour not found with phone number: " + phoneNumber));
    }

    public List<OpenBookingsForLabourDashboardDTO> getOpenBookings(Long labourId) {
        List<LabourAssignment> openAssignments = labourAssignmentRepository.findOpenBookingsForLabour(labourId);

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

        LabourBooking booking = assignment.getBooking();
        dto.setCity(booking.getCity()); //Podapadar
        dto.setZipcode(booking.getZipcode());
        return dto;
    }

    @Transactional
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

        // Get the LabourBooking associated with the assignment
        LabourBooking labourBooking = labourAssignment.getBooking();

        // Check the offer count for the LabourBooking
        int currentOfferCount = labourPriceOfferCountRepository.findByLabourBooking(labourBooking)
                .map(LabourPriceOfferCount::getOfferCount)
                .orElse(0); // Default to 0 if no count record exists

        if (currentOfferCount >= 10) {
            throw new IllegalStateException("Maximum limit of 10 offers reached for this booking");
        }

        // Create a new PriceOffer
        LabourPriceOffer priceOffer = new LabourPriceOffer();
        priceOffer.setLabourAssignment(labourAssignment);
        priceOffer.setLabour(currentLabour);
        priceOffer.setOfferedPrice(priceOfferDTO.getProposedPrice());
        priceOffer.setCreatedAt(LocalDateTime.now());
        priceOffer.setStatus(LabourPriceOfferStatus.PENDING);

        LabourPriceOffer savedOffer = priceOfferRepository.save(priceOffer);

        System.out.println("new price offer saved");

        // update the offer count
        LabourPriceOfferCount offerCount = labourPriceOfferCountRepository.findByLabourBooking(labourBooking)
                .orElseGet(() -> {
                    // Create a new record if not exists
                    LabourPriceOfferCount newOfferCount = new LabourPriceOfferCount();
                    newOfferCount.setLabourBooking(labourBooking);
                    newOfferCount.setOfferCount(0);
                    return newOfferCount;
                });
        offerCount.setOfferCount(offerCount.getOfferCount() + 1);
        labourPriceOfferCountRepository.save(offerCount);

        System.out.println("after the offerCount block");
        return priceOfferRepository.save(savedOffer);
    }
}
