package com.example.PartTimer.controllers.labour;

import com.example.PartTimer.dto.labour.LabourAssignmentDetailsDTO;
import com.example.PartTimer.dto.labour.LabourBookingDTO;
import com.example.PartTimer.dto.labour.LabourBookingsByUserDTO;
import com.example.PartTimer.dto.labour.PriceOfferDetailsDTO;
import com.example.PartTimer.entities.labour.LabourBooking;
import com.example.PartTimer.services.labour.LabourBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/labour-bookings")
public class LabourBookingController {

    @Autowired
    private LabourBookingService labourBookingService;

    @PostMapping
    public ResponseEntity<LabourBooking> createLabourBooking(@RequestBody LabourBookingDTO bookingDTO) {
        LabourBooking createdBooking = labourBookingService.createLabourBooking(bookingDTO);
        return ResponseEntity.ok(createdBooking);
    }

    @GetMapping
    public ResponseEntity<List<LabourBookingsByUserDTO>> getLabourBookingsByUser() {
        List<LabourBookingsByUserDTO> labourBookings = labourBookingService.getLabourBookingsByUser();
        return ResponseEntity.ok(labourBookings);
    }

    @GetMapping("/price-offers/{labourAssignmentId}")
    public ResponseEntity<List<PriceOfferDetailsDTO>> getPriceOffersForAssignment(
            @PathVariable Long labourAssignmentId) {
        List<PriceOfferDetailsDTO> priceeOffers = labourBookingService.getPriceOffersForAssignment(labourAssignmentId);
        return ResponseEntity.ok(priceeOffers);
    }

    //not a part of flow
    @GetMapping("/assignment-details/{labourAssignmentId}")
    public ResponseEntity<LabourAssignmentDetailsDTO> getLabourAssignmentDetails(
            @PathVariable Long labourAssignmentId) {
        LabourAssignmentDetailsDTO details = labourBookingService.getLabourAssignmentDetails(labourAssignmentId);
        return ResponseEntity.ok(details);
    }

    @PostMapping("/accept-offer/{priceOfferId}")
    public ResponseEntity<?> acceptPriceOffer(@PathVariable Long priceOfferId) {
        labourBookingService.acceptPriceOffer(priceOfferId);
        return ResponseEntity.ok().build();
    }
}
