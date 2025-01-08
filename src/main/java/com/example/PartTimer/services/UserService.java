package com.example.PartTimer.services;

import com.example.PartTimer.entities.User;
import com.example.PartTimer.repositories.UserRepository;
import com.example.PartTimer.utils.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionUtil encryptionUtil;

//    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;

    public UserService(PasswordEncoder encoder, AuthenticationManager authenticationManager) {
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
    }

    //sign-up
    public User signUp(User user) {
//        user.setPassword(encoder.encode(user.getPassword()));
//        return userRepository.save(user);
        String encryptedEmail = encryptionUtil.encrypt(user.getEmail());
        user.setEmail(encryptedEmail);
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Login logic (returns user if email and password match)
    public Optional<User> login(String email, String rawPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (encoder.matches(rawPassword, user.getPassword())) {
                return Optional.of(user);  // Successful login
            }
        }
        return Optional.empty();  // Login failed
    }

    public boolean authenticate(String email, String password) {
//        Optional<User> userOptional = userRepository.findByEmail(email);
//
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//
//            // Compare the provided password with the hashed password
//            return encoder.matches(password, user.getPassword());
//        }
//
//        return false; // User not found or password doesn't match

        try {
            // This will throw an exception if authentication fails
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            password
                    )
            );
            return true;
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return false;
        }
    }

    public Optional<User> findByEmail(String email) {
        String encryptedEmail = encryptionUtil.encrypt(email);
        return userRepository.findByEmail(encryptedEmail);
    }
}
