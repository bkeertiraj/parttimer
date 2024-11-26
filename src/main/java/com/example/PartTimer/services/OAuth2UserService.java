package com.example.PartTimer.services;

import com.example.PartTimer.entities.User;
import com.example.PartTimer.entities.UserRole;
import com.example.PartTimer.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        // Extract email from OAuth2User
        String email = oauth2User.getAttribute("email");
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        // Check if user already exists
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update existing user details if needed
            updateExistingUser(user, oauth2User);
        } else {
            user = registerNewUser(oauth2User);
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getUserRole().name())),
                oauth2User.getAttributes(),
                "email"
        );
    }

    private User registerNewUser(OAuth2User oauth2User) {
        User user = new User();

        // Set required fields
        user.setEmail(oauth2User.getAttribute("email"));
        user.setFirstName(oauth2User.getAttribute("given_name"));
        user.setLastName(oauth2User.getAttribute("family_name"));

        // Set default values for required fields
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // Random secure password
        user.setUserRole(UserRole.USER);

        // Optional fields can be left null or set to default values
//        user.setTitle("Mr./Ms.");
//        user.setMiddleName("");

        return userRepository.save(user);
    }

    private void updateExistingUser(User user, OAuth2User oauth2User) {
        // Update any user details that might have changed
        user.setFirstName(oauth2User.getAttribute("given_name"));
        user.setLastName(oauth2User.getAttribute("family_name"));
        userRepository.save(user);
    }
}
