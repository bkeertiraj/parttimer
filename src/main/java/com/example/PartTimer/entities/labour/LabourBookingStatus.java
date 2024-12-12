package com.example.PartTimer.entities.labour;

public enum LabourBookingStatus {
    OPEN,               // Labour hasn't quoted
    PRICE_OFFERED, //
    ACCEPTED,    // User has okayed a price
    COMPLETED     // User agreed to the proposed price
}
