package com.example.PartTimer.entities;

public enum BookingStatus {
    POSTED,
    REQUEST_SENT,
    CONFIRMED,
    INITIATED,
    PAYMENT_PENDING,
    PAYMENT_SUBMITTED,
    COMPLETED;

    public static BookingStatus fromFrontendStatus(String status) {
        return valueOf(status.toUpperCase().replace(' ', '_'));
    }

    public String toFrontendStatus() {
        return this.name().toLowerCase().replace('_', ' ');
    }
}
