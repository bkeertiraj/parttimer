package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.AuthHeaderRequest;
import com.example.PartTimer.dto.CheckUserRequest;
import com.example.PartTimer.dto.CheckUserResponse;
import com.example.PartTimer.dto.UserDTO;
import com.example.PartTimer.entities.User;
import com.example.PartTimer.repositories.UserRepository;
import com.example.PartTimer.services.CustomUserDetailsService;
import com.example.PartTimer.services.JwtService;
import com.example.PartTimer.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @Autowired
    private Key jwtKey;

    //private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Autowired
    private UserService userService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    JwtService jwtService;

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
//        user.setAddress(userDTO.getLocation());

        user.setCountry(userDTO.getCountry());
        user.setState(userDTO.getState());
        user.setCity(userDTO.getCity());
        user.setZipcode(userDTO.getZipcode());

        System.out.println("Received signup request at /register");
        System.out.println("User data: " + user);
        System.out.println("Received signup request for user: " + user.getEmail());
        System.out.println("User details: " + user);
        System.out.println("User title: " + user.getTitle());
        System.out.println("User name: " + user.getFirstName() + user.getMiddleName() + user.getLastName());
        System.out.println("User password: " +user.getPassword() );
//        System.out.println("Address: " + user.getAddress());
        System.out.println("User country: " + user.getCountry());
        System.out.println("User state: " + user.getState());
        System.out.println("User city: " + user.getState());
        System.out.println("User zipcode: " + user.getZipcode());

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
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginData, HttpServletResponse response) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        if (userService.authenticate(email, password)) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            String token = jwtService.generateToken(userDetails);

            //create HTTP-only cookie
            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setHttpOnly(true); // prevents JavaScript access
            jwtCookie.setSecure(true); //send only over HTTPS
            jwtCookie.setPath("/"); //available across the entire application
            jwtCookie.setMaxAge(2 * 24 * 60 * 60); //2 days in seconds

            // Add cookie to response
            System.out.println("cookie issued: " + jwtCookie.getName());
            response.addCookie(jwtCookie);

            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            System.out.println("authHeader provided: " + authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "No authorization token provided"));
            }
            String token = authHeader.substring(7);
            String userEmail = jwtService.extractUsername(token);

            if (userEmail == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token"));
            }

            Optional<User> userOpt = userService.findByEmail(userEmail);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("user_id", user.getUserId());
            response.put("name", user.getFullName());
            response.put("email", user.getEmail());
            response.put("user_role", user.getUserRole());

            if (user.getOrganization() != null) {
                Map<String, Object> orgDetails = new HashMap<>();
                orgDetails.put("id", user.getOrganization().getId());
                orgDetails.put("name", user.getOrganization().getName());
                response.put("organization", orgDetails);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing request: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // Create a cookie with 0 max age to delete it
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Expire immediately

        // Add cookie to response
        response.addCookie(jwtCookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    @Autowired
    UserRepository userRepository;

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication exists and is not anonymous
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof UsernamePasswordAuthenticationToken &&
                        authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized access"));
        }


        String username;
        if (authentication.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid authentication principal"));
        }


//        // Retrieve user details from authentication
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

//        Optional<User> thisuser = userService.findByEmail(userDetails.getUsername());
//        User currentuser = thisuser.get();
//        Map<String, Object> response = new HashMap<>();
//
//        System.out.println("user details: " + currentuser.getFullName() +" " + currentuser.getUserId());
//
//        // Prepare response
//        Map<String, Object> response = new HashMap<>();
//        response.put("user_id", currentuser.getUserId());
//        response.put("name", currentuser.getFullName());
//        response.put("email", currentuser.getEmail());
//        response.put("user_role", currentuser.getUserRole());
//
//        if (currentuser.getOrganization() != null) {
//            Map<String, Object> orgDetails = new HashMap<>();
//            orgDetails.put("id", currentuser.getOrganization().getId());
//            orgDetails.put("name", currentuser.getOrganization().getName());
//            response.put("organization", orgDetails);
//        }
//
//        return ResponseEntity.ok(response);
        Optional<User> thisuser = userService.findByEmail(username);
        if (thisuser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        User currentuser = thisuser.get();
        Map<String, Object> response = new HashMap<>();
        response.put("user_id", currentuser.getUserId());
        response.put("name", currentuser.getFullName());
        response.put("email", currentuser.getEmail());
        response.put("user_role", currentuser.getUserRole());

        if (currentuser.getOrganization() != null) {
            Map<String, Object> orgDetails = new HashMap<>();
            orgDetails.put("id", currentuser.getOrganization().getId());
            orgDetails.put("name", currentuser.getOrganization().getName());
            response.put("organization", orgDetails);
        }

        return ResponseEntity.ok(response);
    }

//    @GetMapping("/oauth2/google")
//    public ResponseEntity<String> oauth2Authorization() {
//        return ResponseEntity.ok("OAuth2 authorization URL");
//    }


    @PostMapping("/check-user")
    public ResponseEntity<CheckUserResponse> checkUser(@RequestBody CheckUserRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            // User does not exist
            return ResponseEntity.ok(new CheckUserResponse(false, false, ""));
        }
        User user = userOptional.get();
        // Check profile completeness
        List<String> missingFields = new ArrayList<>();

        if (user.getCountry() == null || user.getCountry().isEmpty()) {
            missingFields.add("country");
        }
        if (user.getState() == null || user.getState().isEmpty()) {
            missingFields.add("state");
        }
        if (user.getCity() == null || user.getCity().isEmpty()) {
            missingFields.add("city");
        }
        if (user.getZipcode() == null || user.getZipcode().isEmpty()) {
            missingFields.add("zipcode");
        }
        boolean isProfileComplete = missingFields.isEmpty();
        String missingFieldsStr = String.join(", ", missingFields);

        return ResponseEntity.ok(new CheckUserResponse(
                true,
                isProfileComplete,
                missingFieldsStr
        ));
    }
}
