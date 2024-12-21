package com.example.PartTimer.controllers.labour;

import com.example.PartTimer.dto.labour.LabourPriceHistoryDTO;
import com.example.PartTimer.dto.labour.LabourPriceOfferDetailsDTO;
import com.example.PartTimer.services.labour.LabourDashboardService;
import com.example.PartTimer.services.labour.LabourPriceHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/labour")
@RequiredArgsConstructor
public class LabourPriceHistoryController {

    private final LabourPriceHistoryService labourPriceHistoryService;
    private final LabourDashboardService labourDashboardService;

    @GetMapping("/price-history")
    public ResponseEntity<List<LabourPriceHistoryDTO>> getPriceHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        List<LabourPriceHistoryDTO> priceHistory = labourPriceHistoryService.getLabourPriceHistory(labourDashboardService.getCurrentLabourId());
        return ResponseEntity.ok(priceHistory);
    }

    @GetMapping("/{offerId}/details")
    public ResponseEntity<LabourPriceOfferDetailsDTO> getPriceOfferDetails(@PathVariable Long offerId) {
        LabourPriceOfferDetailsDTO details = labourPriceHistoryService.getPriceOfferDetails(offerId);
        return ResponseEntity.ok(details);
    }
}
