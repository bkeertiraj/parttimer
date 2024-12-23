package com.example.PartTimer.services.labour;

import com.example.PartTimer.dto.labour.LabourPriceHistoryDTO;
import com.example.PartTimer.dto.labour.LabourPriceOfferDetailsDTO;
import com.example.PartTimer.entities.User;
import com.example.PartTimer.entities.labour.LabourAssignment;
import com.example.PartTimer.entities.labour.LabourBooking;
import com.example.PartTimer.entities.labour.LabourPriceOffer;
import com.example.PartTimer.entities.labour.LabourPriceOfferStatus;
import com.example.PartTimer.repositories.labour.LabourPriceOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabourPriceHistoryService {

    private final LabourPriceOfferRepository labourPriceOfferRepository;

    public List<LabourPriceHistoryDTO> getLabourPriceHistory(Long labourId) {
        List<LabourPriceOffer> priceOffers = labourPriceOfferRepository.findByLabourIdOrderByCreatedAtDesc(labourId);

        return priceOffers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private LabourPriceHistoryDTO convertToDTO(LabourPriceOffer offer) {
        LabourPriceHistoryDTO dto = new LabourPriceHistoryDTO();
        dto.setOfferId(offer.getId());
        dto.setAssignmentId(offer.getLabourAssignment().getId());
        dto.setBookingId(offer.getLabourAssignment().getBooking().getId());
        dto.setTimeSlot(offer.getLabourAssignment().getTimeSlot());
        dto.setOfferedPrice(offer.getOfferedPrice());
        dto.setStatus(offer.getStatus());
        dto.setBookingDate(offer.getLabourAssignment().getBookingDate());
        LabourBooking booking = offer.getLabourAssignment().getBooking();
        dto.setCity(booking.getCity());
        dto.setZipCode(booking.getZipcode());
        return dto;
    }


    public LabourPriceOfferDetailsDTO getPriceOfferDetails(Long offerId) {
        LabourPriceOffer offer = labourPriceOfferRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Price offer not found"));

        LabourPriceOfferDetailsDTO detailsDTO = new LabourPriceOfferDetailsDTO();

        switch (offer.getStatus()) {
            case PENDING:
                populatePendingDetails(detailsDTO, offer);
                break;

            case ACCEPTED:
                populateAcceptedDetails(detailsDTO, offer);
                break;
            case WITHDRAWN:
                populateWithdrawnDetails(detailsDTO, offer);
                break;
            default:
                throw new IllegalArgumentException("Invalid price offer status: " + offer.getStatus());
        }
        return detailsDTO;
    }

    private void populatePendingDetails(LabourPriceOfferDetailsDTO detailsDTO, LabourPriceOffer offer) {
        LabourAssignment assignment = offer.getLabourAssignment();

        detailsDTO.setBookingDate(assignment.getBookingDate());
        detailsDTO.setBookingNote(assignment.getBookingNote());
        detailsDTO.setBookingStatus(offer.getStatus());
        detailsDTO.setTimeSlot(assignment.getTimeSlot());
        detailsDTO.setStatus(LabourPriceOfferStatus.PENDING);
    }

    private void populateAcceptedDetails(LabourPriceOfferDetailsDTO detailsDTO, LabourPriceOffer offer) {
        LabourAssignment assignment = offer.getLabourAssignment();
        LabourBooking booking = assignment.getBooking();
        User user = booking.getUser();

        detailsDTO.setBookingDate(assignment.getBookingDate());
        detailsDTO.setBookingNote(assignment.getBookingNote());
        detailsDTO.setBookingStatus(offer.getStatus());
        detailsDTO.setTimeSlot(assignment.getTimeSlot());
        detailsDTO.setUserPhoneNumber(user.getPhoneNumber());
        detailsDTO.setUserEmail(user.getEmail());
        detailsDTO.setBookingAddress(booking.getAddress());
        detailsDTO.setStatus(LabourPriceOfferStatus.ACCEPTED);
    }


    private void populateWithdrawnDetails(LabourPriceOfferDetailsDTO detailsDTO, LabourPriceOffer offer) {

        LabourAssignment assignment = offer.getLabourAssignment();
        LabourBooking booking = assignment.getBooking();
        // Find the accepted offer for this booking (by other labours)
        LabourPriceOffer acceptedOffer = labourPriceOfferRepository
                .findFirstByLabourAssignmentAndStatus(assignment, LabourPriceOfferStatus.WITHDRAWN)
                .orElseThrow(() -> new RuntimeException("No accepted price offer found for this booking"));

        detailsDTO.setBookingDate(assignment.getBookingDate());
        detailsDTO.setBookingNote(assignment.getBookingNote());
        detailsDTO.setBookingStatus(offer.getStatus());
        detailsDTO.setTimeSlot(assignment.getTimeSlot());
        detailsDTO.setAcceptedPrice(acceptedOffer.getOfferedPrice());
        detailsDTO.setAcceptedLabourRating(acceptedOffer.getLabour().getAverageRating()); // Assuming Labour entity has a rating field
        detailsDTO.setStatus(LabourPriceOfferStatus.WITHDRAWN);
    }

}
