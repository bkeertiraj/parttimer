package com.example.PartTimer.controllers.labour;

import com.example.PartTimer.dto.labour.LabourPriceOfferDTO;
import com.example.PartTimer.dto.labour.OpenBookingsForLabourDashboardDTO;
import com.example.PartTimer.entities.labour.Labour;
import com.example.PartTimer.entities.labour.LabourAssignment;
import com.example.PartTimer.entities.labour.LabourPriceOffer;
import com.example.PartTimer.repositories.labour.LabourRepository;
import com.example.PartTimer.services.labour.LabourDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labour-dashboard")
public class LabourDashboardController {

    @Autowired
    private LabourDashboardService labourDashboardService;

    @Autowired
    private LabourRepository labourRepository;

    @GetMapping("/open-bookings")
    public ResponseEntity<List<OpenBookingsForLabourDashboardDTO>> getOpenBookings() {

        List<OpenBookingsForLabourDashboardDTO> openBookings = labourDashboardService.getOpenBookings();
        return ResponseEntity.ok(openBookings);
    }

    @PostMapping("/offer-price")
    public ResponseEntity<LabourPriceOffer> offerPriceForBooking(
            @RequestBody LabourPriceOfferDTO priceOfferDTO) {
        LabourPriceOffer createdPriceOffer = labourDashboardService.offerPriceForBooking(priceOfferDTO);
        return ResponseEntity.ok(createdPriceOffer);
    }
}
