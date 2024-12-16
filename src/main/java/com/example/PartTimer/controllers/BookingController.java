package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.BookingRequestDTO;
import com.example.PartTimer.dto.user_dashboard.UserBookingsDTO;
import com.example.PartTimer.entities.Booking;
import com.example.PartTimer.entities.User;
import com.example.PartTimer.repositories.UserRepository;
import com.example.PartTimer.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/book")
    public ResponseEntity<Booking> bookService(@RequestBody BookingRequestDTO bookingRequest) {
        //bookingRequest.setServiceId(serviceId);
        Booking booking = bookingService.createBooking(bookingRequest);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserBookingsDTO>> getUserBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserBookingsDTO> bookings = bookingService.getUserBookings(user);
        return ResponseEntity.ok(bookings);
    }
}
