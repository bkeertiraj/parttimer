package com.example.PartTimer.services;

import com.example.PartTimer.dto.stripe.ProductRequest;
import com.example.PartTimer.dto.stripe.StripeResponse;
import com.example.PartTimer.entities.User;
import com.example.PartTimer.repositories.UserRepository;
import com.example.PartTimer.utils.EncryptionUtil;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class StripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;

//    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(StripeService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    EncryptionUtil encryptionUtil;

    public StripeResponse checkoutProducts(ProductRequest productRequest) {
        Stripe.apiKey = secretKey;

        //get current authenticated user's email
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("Inside checkoutProducts");

        if (authentication == null) {
            System.err.println("SecurityContextHolder has no authentication");
            throw new IllegalStateException("No authentication found in SecurityContext");
        }

        // More detailed authentication checks
        String userEmail = null;
        try {
            userEmail = authentication.getName();
            System.out.println("Authentication name: " + userEmail);

            if (userEmail == null || "anonymousUser".equals(userEmail)) {
                System.err.println("User is not authenticated");
                throw new IllegalStateException("User is not authenticated");
            }
        } catch (Exception e) {
            System.err.println("Error extracting authentication: " + e.getMessage());
            throw new IllegalStateException("Could not extract user authentication", e);
        }

        String encryptedEmail = encryptionUtil.encrypt(userEmail);
        Optional<User> userOptional = userRepository.findByEmail(encryptedEmail);
        System.out.println("User email in checkout: " + userEmail);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("User email from checking the email: " + user.getEmail());
        }
        System.out.println();

        // Create product data
        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productRequest.getGems() + "Gems")
                        .build();

        // Create price data
        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("USD")
                        .setUnitAmount(productRequest.getPrice())
                        .setProductData(productData)
                        .build();


        // Create new line item
        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build();

        // Create payment intent parameters with metadata
//        PaymentIntentCreateParams paymentIntentParams =
//                PaymentIntentCreateParams.builder()
//                        .putMetadata("user_email", userEmail)
//                        .putMetadata("gems", String.valueOf(productRequest.getGems()))
//                        .build();

        //explicitly creating a map for metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("user_email", userEmail);
        metadata.put("gems", String.valueOf(productRequest.getGems()));

        // Create new session with the line items
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:5173/points?status=success&amount=" + productRequest.getGems())
                        .setCancelUrl("http://localhost:5173/points?status=error")
                        .addLineItem(lineItem)
                        .putAllMetadata(metadata)
                        .setPaymentIntentData(
                                SessionCreateParams.PaymentIntentData.builder()
                                        .putAllMetadata(metadata)
                                        .build()
                        )
                        .build();

        // Create new session
        Session session = null;
        try {
            session = Session.create(params);
            // Extensive logging
            System.out.println("Stripe Session Created:");
            System.out.println("Session ID: " + session.getId());
            System.out.println("Session Metadata: " + session.getMetadata());
            System.out.println("Session URL: " + session.getUrl());
        } catch (StripeException e) {
            //log the error
            throw new RuntimeException("Error creating Stripe session", e);
        }

        return StripeResponse
                .builder()
                .status("SUCCESS")
                .message("Payment session created")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }

}
