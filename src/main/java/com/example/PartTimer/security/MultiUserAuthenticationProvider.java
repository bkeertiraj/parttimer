package com.example.PartTimer.security;

import com.example.PartTimer.entities.User;
import com.example.PartTimer.entities.labour.Labour;
import com.example.PartTimer.repositories.UserRepository;
import com.example.PartTimer.repositories.labour.LabourRepository;
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
import java.util.Optional;

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

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        // First, check User table
        Optional<User> userOptional = userRepository.findByEmail(email);
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
