package com.example.PartTimer.services;

import com.example.PartTimer.entities.User;
import com.example.PartTimer.entities.labour.Labour;
import com.example.PartTimer.repositories.UserRepository;
import com.example.PartTimer.repositories.labour.LabourRepository;
import com.example.PartTimer.utils.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final LabourRepository labourRepository;

    @Autowired
    EncryptionUtil encryptionUtil;

    public CustomUserDetailsService(UserRepository userRepository, LabourRepository labourRepository) {
        this.userRepository = userRepository;
        this.labourRepository = labourRepository;
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
//
//        return org.springframework.security.core.userdetails.User
//                .withUsername(user.getEmail())
//                .password(user.getPassword())
//                .roles(user.getUserRole().name())
//                .build();

        // First, try to find in User table
        String encryptedEmail = encryptionUtil.encrypt(email);
        Optional<User> userOptional = userRepository.findByEmail(encryptedEmail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new org.springframework.security.core.userdetails.User(
                    email,
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(user.getUserRole().name()))
            );
        }

        // Then, try to find in Labour table
        Optional<Labour> labourOptional = labourRepository.findByPhoneNumber(email); // Assuming phone number is used for login
        if (labourOptional.isPresent()) {
            Labour labour = labourOptional.get();
            return new org.springframework.security.core.userdetails.User(
                    labour.getPhoneNumber(),
                    labour.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_LABOUR"))
            );
        }

        throw new UsernameNotFoundException("User not found with email/phone: " + email);
    }
}
