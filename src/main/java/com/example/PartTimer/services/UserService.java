package com.example.PartTimer.services;

import com.example.PartTimer.entities.User;
import com.example.PartTimer.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    //sign-up
    public User signUp(User user) {
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
}
