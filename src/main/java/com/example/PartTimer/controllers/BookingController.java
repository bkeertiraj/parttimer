package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.BookingRequestDTO;
import com.example.PartTimer.entities.Booking;
import com.example.PartTimer.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/{serviceId}/book")
    public ResponseEntity<Booking> bookService(@PathVariable Long serviceId,  @RequestBody BookingRequestDTO bookingRequest) {
        bookingRequest.setServiceId(serviceId);
        Booking booking = bookingService.createBooking(bookingRequest);
        return ResponseEntity.ok(booking);
    }
}
