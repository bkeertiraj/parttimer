package com.example.PartTimer.entities;

public enum BookingStatus {
    OPEN, //POSTED
    SELLER_SELECTED, //earlier REQUEST_SENT
    SELLER_ACCEPTED, //earlier CONFIRMED
    INITIATED,
    PAYMENT_PENDING,
    PAYMENT_SUBMITTED,
    COMPLETED;

    public static BookingStatus fromFrontendStatus(String status) {
        return valueOf(status.toUpperCase().replace(' ', '_'));
    }

    public String toFrontendStatus() {
        //return this.name().toLowerCase().replace('_', ' ');
        return this.name();//.replace('_', ' ');
    }
}
