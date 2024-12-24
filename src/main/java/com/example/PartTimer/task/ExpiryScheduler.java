package com.example.PartTimer.task;

import com.example.PartTimer.entities.labour.*;
import com.example.PartTimer.repositories.labour.LabourAssignmentRepository;
import com.example.PartTimer.repositories.labour.LabourBookingRepository;
import com.example.PartTimer.repositories.labour.LabourPriceOfferRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ExpiryScheduler {

    @Autowired
    private LabourBookingRepository labourBookingRepository;

    @Autowired
    private LabourPriceOfferRepository labourPriceOfferRepository;

    @Autowired
    private LabourAssignmentRepository labourAssignmentRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") //runs at midnight everyday
    public void updateExpiredBookingsAndOffers() {
        // Fetch labour assignments that are not completed or expired and are past their booking date
        List<LabourAssignment> expiredAssignments = labourAssignmentRepository.findByBookingDateBeforeAndBookingStatusNotIn(
                LocalDate.now(),
                List.of(LabourBookingStatus.COMPLETED, LabourBookingStatus.EXPIRED)
        );
        // Update each expired assignment
        for (LabourAssignment assignment : expiredAssignments) {
            assignment.setBookingStatus(LabourBookingStatus.EXPIRED);
            assignment.setStatusChangedAt(LocalDate.now().atStartOfDay()); // Mark when status was changed
            labourAssignmentRepository.save(assignment);

            // Update corresponding price offers to reflect booking expiration
            List<LabourPriceOffer> priceOffers = labourPriceOfferRepository.findByLabourAssignment(assignment);
            for (LabourPriceOffer offer : priceOffers) {
                offer.setStatus(LabourPriceOfferStatus.EXPIRED);
                labourPriceOfferRepository.save(offer);
            }
        }
    }
}
