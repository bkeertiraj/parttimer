package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.UserDTO;
import com.example.PartTimer.entities.User;
import com.example.PartTimer.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // Sign-up endpoint
    @PostMapping("/register")
    public ResponseEntity<String> signUp(@RequestBody UserDTO userDTO) {
        User user = new User();
        user.setTitle(userDTO.getNamePrefix());
        user.setFirstName(userDTO.getFirstName());
        user.setMiddleName(userDTO.getMiddleName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getLocation());

        System.out.println("Received signup request at /register");
        System.out.println("User data: " + user);
        System.out.println("Received signup request for user: " + user.getEmail());
        System.out.println("User details: " + user);
        System.out.println("User title: " + user.getTitle());
        System.out.println("User name: " + user.getFirstName() + user.getMiddleName() + user.getLastName());
        System.out.println("User password: " +user.getPassword() );
        System.out.println("Address: " + user.getAddress());
        System.out.println("Title: " + user.getTitle());

        try {
            userService.signUp(user);
            System.out.println("User registered successfully: " + user.getEmail());
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error registering user: " + e.getMessage());
        }
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        Optional<User> user = userService.login(email, password);
        if (user.isPresent()) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
}
