package com.example.PartTimer.controllers.labour;

import com.example.PartTimer.dto.labour.EditPriceOfferDTO;
import com.example.PartTimer.dto.labour.LabourPendingOffersDTO;
import com.example.PartTimer.dto.labour.LabourPriceOfferDTO;
import com.example.PartTimer.dto.labour.OpenBookingsForLabourDashboardDTO;
import com.example.PartTimer.entities.labour.Labour;
import com.example.PartTimer.entities.labour.LabourAssignment;
import com.example.PartTimer.entities.labour.LabourPriceOffer;
import com.example.PartTimer.repositories.labour.LabourRepository;
import com.example.PartTimer.services.labour.LabourDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/labour-dashboard")
public class LabourDashboardController {

    @Autowired
    private LabourDashboardService labourDashboardService;

    @Autowired
    private LabourRepository labourRepository;

    @GetMapping("/open-bookings")
    public ResponseEntity<List<OpenBookingsForLabourDashboardDTO>> getOpenBookings() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();
        Optional<Labour> labourOptional = labourRepository.findByPhoneNumber(phoneNumber);
        Labour labour = labourOptional.get();
        List<OpenBookingsForLabourDashboardDTO> openBookings = labourDashboardService.getOpenBookings(labour.getId());
        return ResponseEntity.ok(openBookings);
    }

    @PostMapping("/offer-price")
    public ResponseEntity<LabourPriceOffer> offerPriceForBooking(
            @RequestBody LabourPriceOfferDTO priceOfferDTO) {
        LabourPriceOffer createdPriceOffer = labourDashboardService.offerPriceForBooking(priceOfferDTO);
        return ResponseEntity.ok(createdPriceOffer);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<LabourPendingOffersDTO>> getPendingOffers() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();
        Optional<Labour> labourOptional = labourRepository.findByPhoneNumber(phoneNumber);
        Labour labour = labourOptional.get();
        return ResponseEntity.ok(labourDashboardService.getPendingOffersByLabour(labour.getId()));
    }

    @PutMapping("/edit-offer/{offerId}")
    public ResponseEntity<?> editPriceOffer(
            @PathVariable Long offerId,
            @RequestBody EditPriceOfferDTO editDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phoneNumber = authentication.getName();
            Optional<Labour> labourOptional = labourRepository.findByPhoneNumber(phoneNumber);
            Labour labour = labourOptional.get();
            Long labourId = labour.getId(); // Implement this based on your auth setup

            // Validate that the path variable matches the DTO
            if (!offerId.equals(editDTO.getPriceOfferId())) {
                return ResponseEntity.badRequest()
                        .body("Path variable offerId doesn't match the request body");
            }

            LabourPriceOffer updatedOffer = labourDashboardService.editPriceOffer(labourId, editDTO);
            return ResponseEntity.ok(updatedOffer);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating price offer: " + e.getMessage());
        }
    }
}
