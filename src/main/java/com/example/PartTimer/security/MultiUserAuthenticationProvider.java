package com.example.PartTimer.security;

import com.example.PartTimer.entities.User;
import com.example.PartTimer.entities.labour.Labour;
import com.example.PartTimer.repositories.UserRepository;
import com.example.PartTimer.repositories.labour.LabourRepository;
import com.example.PartTimer.utils.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Lazy
public class MultiUserAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final LabourRepository labourRepository;
    private final PasswordEncoder passwordEncoder;


//    @Autowired
    public MultiUserAuthenticationProvider(
            @Lazy UserRepository userRepository,
            @Lazy LabourRepository labourRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.labourRepository = labourRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    EncryptionUtil encryptionUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        // First, check User table
        System.out.println("Raw login email: " + email);
        String encryptedEmail = encryptionUtil.encrypt(email);
        System.out.println("Encrypted login email: " + encryptedEmail);

        // Query database directly to see what's stored
        List<User> allUsers = userRepository.findAll();
        System.out.println("All users in database:");
        for (User user : allUsers) {
            System.out.println("Stored email: " + user.getEmail());
        }

        Optional<User> userOptional = userRepository.findByEmail(encryptedEmail);
        System.out.println("User found: " + userOptional.isPresent());

        // Add this debug section
        if (!userOptional.isPresent()) {
            System.out.println("No user found with encrypted email: " + encryptedEmail);
            // Maybe add a database query to show all encrypted emails for debugging
            List<String> allEmails = userRepository.findAll().stream()
                    .map(User::getEmail)
                    .collect(Collectors.toList());
            System.out.println("Available encrypted emails in database: " + allEmails);
        }

        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    email,
                    userOptional.get().getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(userOptional.get().getUserRole().name()))
            );
            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        }

        // Then, check Labour table
        Optional<Labour> labourOptional = labourRepository.findByPhoneNumber(email); // Assuming phone number is used for login
        if (labourOptional.isPresent() && passwordEncoder.matches(password, labourOptional.get().getPassword())) {
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    email,
                    labourOptional.get().getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_LABOUR"))
            );
            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        }

        throw new BadCredentialsException("Invalid credentials");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }


}
