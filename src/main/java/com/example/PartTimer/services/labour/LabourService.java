package com.example.PartTimer.services.labour;

import com.example.PartTimer.entities.labour.Labour;
import com.example.PartTimer.repositories.labour.LabourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LabourService {

    @Autowired
    private LabourRepository labourRepository;

    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;

    public LabourService(PasswordEncoder encoder, AuthenticationManager authenticationManager) {
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
    }

    public Labour signUp(Labour labour) {
        labour.setPassword(encoder.encode(labour.getPassword()));
        return labourRepository.save(labour);
    }
}
