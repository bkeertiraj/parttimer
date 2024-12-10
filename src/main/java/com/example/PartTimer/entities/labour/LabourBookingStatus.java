package com.example.PartTimer.entities.labour;

public enum LabourBookingStatus {
    OPEN,               // Labour hasn't quoted
    LABOUR_SELECTED,    // Labour has requested a price
    LABOUR_ACCEPTED     // User agreed to the proposed price
}
