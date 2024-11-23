package com.example.PartTimer.services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${resend.from.email}")
    private String fromEmail;

    private final Map<String, String> otpStore = new HashMap<>();

    public String generateAndSendOTP(String toEmail) throws ResendException {

        String otp = generateOTP();
        otpStore.put(toEmail, otp);

        String htmlContent = String.format(
                "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
                        "<h2>Email Verification</h2>" +
                        "<p>Your verification code is: <strong>%s</strong></p>" +
                        "<p>This code will expire in 10 minutes.</p>" +
                        "</div>",
                otp
        );

        //send email using Resend
        Resend resend = new Resend(resendApiKey);
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(toEmail)
                .subject("Email verification Code")
                .html(htmlContent)
                .build();

        CreateEmailResponse response = resend.emails().send(params);
        return response.getId();
    }

    public boolean verifyOTP(String email, String otp) {
        String storedOTP = otpStore.get(email);
        if (storedOTP != null && storedOTP.equals(otp)) {
            otpStore.remove(email); // Remove OTP after successful verification
            return true;
        }
        return false;
    }

    private String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

}
