package com.example.PartTimer.entities;

public enum ServiceRequestStatus {
    POSTED,
    REQUEST_SENT, //
    CONFIRMED,
    INITIATED,
    PAYMENT_PENDING,
    PAYMENT_SUBMITTED,
    COMPLETED;

    public String toFrontendStatus() {
        return this.name().toLowerCase().replace('_', ' ');
    }

    public static ServiceRequestStatus fromFrontendStatus(String frontendStatus) {
        return valueOf(frontendStatus.toUpperCase().replace(' ', '_'));
    }
}
