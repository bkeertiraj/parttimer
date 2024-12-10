package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.stripe.ProductRequest;
import com.example.PartTimer.dto.stripe.StripeResponse;
import com.example.PartTimer.entities.User;
import com.example.PartTimer.repositories.UserRepository;
import com.example.PartTimer.services.StripeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/stripe")
public class StripeController {

//    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(StripeService.class);

    private StripeService stripeService;

    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @GetMapping
    public String index(){
        return "index";
    }

    @GetMapping("/success")
    public String success(){
        return "success";
    }

    @PostMapping("/product/v1/checkout")
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody ProductRequest productRequest) {
        System.out.println("price from dto: " + productRequest.getPrice());
        StripeResponse stripeResponse = stripeService.checkoutProducts(productRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;


    @Value("${stripe.secretKey}")
    private String secretKey;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String stripeSignature) {

        System.out.println("INSIDE /webhook successfully");
        System.out.println("Webhook payload: " + payload);
        System.out.println("Stripe Signature: " + stripeSignature);

        try {
            Stripe.apiKey = secretKey;
            System.out.println("Raw Payload: " + payload);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> eventMap = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {
                @Override
                public Type getType() {
                    return super.getType();
                }

                @Override
                public int compareTo(TypeReference<Map<String, Object>> o) {
                    return super.compareTo(o);
                }
            });
            System.out.println("Event Type: " + eventMap.get("type"));
            System.out.println("Full Event Details: " + objectMapper.writeValueAsString(eventMap));

            Event event = Webhook.constructEvent(payload, stripeSignature, webhookSecret);

            if("checkout.session.completed".equals(event.getType())
//                    ||
//                    "payment_intent.succeeded".equals(event.getType()) ||
//                    "charge.succeeded".equals(event.getType())
            ) {

                String paymentIntentId = null;
                Map<String, String> metadata = null;

//                Session session = (Session) event.getData().getObject();

                // Handle different event types
                if (event.getData().getObject() instanceof Session) {
                    Session session = (Session) event.getData().getObject();
                    paymentIntentId = session.getPaymentIntent();
                    metadata = session.getMetadata();
                } else if (event.getData().getObject() instanceof Charge) {
                    Charge charge = (Charge) event.getData().getObject();
                    paymentIntentId = charge.getPaymentIntent();
                }

                if(paymentIntentId != null) {
                    PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                    metadata = paymentIntent.getMetadata();

                    System.out.println("is metadata null: "+metadata.isEmpty());
                    if(metadata!= null &&
                            metadata.containsKey("user_email") &&
                            metadata.containsKey("gems")) {

                        System.out.println("metadata found: "+metadata);
                        String userEmail = metadata.get("user_email");
                        int gemsToAdd = Integer.parseInt(metadata.get("gems"));

                        //find user and add gems
                        Optional<User> userOptional = userRepository.findByEmail(userEmail);
                        if(userOptional.isPresent()) {
                            User user = userOptional.get();
                            System.out.println("current gem count of the user: "+user.getPoints());
                            user.setPoints(user.getPoints() + gemsToAdd);
                            userRepository.save(user);
                            System.out.println("Gems added successfully for user: " + userEmail);
                        }
                    }
                }

            }
            return ResponseEntity.ok("Webhook processed successfully");
        }

        catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid webhook signature");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }

    @GetMapping("/user/gems")
    public ResponseEntity<Map<String, Integer>> getUserGems() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();;
        Optional<User> userOptional = userRepository.findByEmail(authentication.getName());
        Map<String, Integer> response = new HashMap<>();

        if(userOptional.isPresent()) {
            response.put("gemCount", userOptional.get().getPoints());
            return ResponseEntity.ok(response);
        }
        //if no user found
        response.put("gemCount", 0);
        return ResponseEntity.ok(response);
    }

}
