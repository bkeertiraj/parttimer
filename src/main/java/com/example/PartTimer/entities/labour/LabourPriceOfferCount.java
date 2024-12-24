package com.example.PartTimer.entities.labour;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class LabourPriceOfferCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "labour_booking_id", nullable = false)
    private LabourBooking labourBooking;

    private int offerCount;

    @Version
    private int version;
}
