package com.example.PartTimer.services.labour;

import com.example.PartTimer.dto.labour.LabourPriceHistoryDTO;
import com.example.PartTimer.entities.labour.LabourPriceOffer;
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
        dto.setBookingAddress(offer.getLabourAssignment().getBooking().getAddress());
        dto.setTimeSlot(offer.getLabourAssignment().getTimeSlot());
        dto.setOfferedPrice(offer.getOfferedPrice());
        dto.setStatus(offer.getStatus());
        dto.setBookingDate(offer.getLabourAssignment().getBookingDate());
        dto.setCity("Podapadar");
        dto.setZipCode("761026");
        return dto;
    }
}
