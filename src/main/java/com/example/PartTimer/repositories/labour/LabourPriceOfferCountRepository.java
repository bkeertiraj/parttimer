package com.example.PartTimer.repositories.labour;

import com.example.PartTimer.entities.labour.LabourBooking;
import com.example.PartTimer.entities.labour.LabourPriceOfferCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabourPriceOfferCountRepository extends JpaRepository<LabourPriceOfferCount, Long> {

    Optional<LabourPriceOfferCount> findByLabourBooking(LabourBooking labourBooking);

    Optional<LabourPriceOfferCount> findByLabourBookingId(Long labourBookingId);
}
