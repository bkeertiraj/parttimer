package com.example.PartTimer.services;

import com.resend.core.exception.ResendException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${resend.from.email}")
    private String fromEmail;

    private final Map<String, String> otpStore = new HashMap<>();

//    public String generateAndSendOTP(String toEmail) throws ResendException {
//
//
//    }
}
