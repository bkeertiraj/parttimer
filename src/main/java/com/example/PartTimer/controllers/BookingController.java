package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.BookingRequestDTO;
import com.example.PartTimer.dto.user_dashboard.UserBookingsDTO;
import com.example.PartTimer.entities.Booking;
import com.example.PartTimer.entities.User;
import com.example.PartTimer.repositories.UserRepository;
import com.example.PartTimer.services.BookingService;
import com.example.PartTimer.utils.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionUtil encryptionUtil;

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

        String encryptedEmail = encryptionUtil.encrypt(userEmail);
        Optional<User> userOptional = userRepository.findByEmail(encryptedEmail);
        User user = userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserBookingsDTO> bookings = bookingService.getUserBookings(user);
        return ResponseEntity.ok(bookings);
    }
}
