package com.example.PartTimer.config;

import com.example.PartTimer.entities.User;
import com.example.PartTimer.repositories.UserRepository;
import com.example.PartTimer.repositories.labour.LabourRepository;
import com.example.PartTimer.security.JwtAuthenticationFilter;
import com.example.PartTimer.security.MultiUserAuthenticationProvider;
import com.example.PartTimer.services.CustomUserDetailsService;
import com.example.PartTimer.services.JwtService;
import com.example.PartTimer.services.OAuth2UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class WebSecurityConfig {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtAuthenticationFilter jwtFilter;
    private final OAuth2UserService oAuth2UserService;
    private final UserRepository userRepository;

//    @Lazy
    private final MultiUserAuthenticationProvider multiUserAuthenticationProvider;

    public WebSecurityConfig(JwtService jwtService,
                             UserDetailsService userDetailsService,
                             HandlerExceptionResolver handlerExceptionResolver,
                             JwtAuthenticationFilter jwtFilter, UserRepository userRepository, OAuth2UserService oAuth2UserService, @Lazy MultiUserAuthenticationProvider multiUserAuthenticationProvider) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtFilter = jwtFilter;
        this.oAuth2UserService = oAuth2UserService;
        this.userRepository = userRepository;
        this.multiUserAuthenticationProvider = multiUserAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(multiUserAuthenticationProvider)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
//                                .requestMatchers(
//                                        "/api/auth/**",
//                                        "/api/auth/register",
//                                        "/api/auth/login",
//                                        "/api/services",
//                                        "/api/locations/**",
//                                        "/api/auth/current-user",
//                                        "/api/email/**",
//                                        "/oauth2/authorize/**",
//                                        "/login/oauth2/code/**",
//                                        "/oauth2/authorize/google",
//                                        "/oauth2/**",
//                                        "/login/oauth2/**",
//                                        "/api/auth/**",
//                                        "/stripe/webhook").permitAll()
//                                .requestMatchers("/error").permitAll()
//                                .anyRequest().authenticated()
                                .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorizationEndpoint ->
                                authorizationEndpoint.baseUri("/oauth2/authorize"))
                        .redirectionEndpoint(redirectionEndpoint ->
                                redirectionEndpoint.baseUri("/login/oauth2/code/google"))


                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                            String email = oauth2User.getAttribute("email");
                            if (email == null) {
                                throw new IllegalStateException("Email not found in OAuth2 response");
                            }
                            // Fetch UserDetails using the email
                            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                            User userEntity = userRepository.findByEmail(email)
                                    .orElseThrow(() -> new IllegalStateException("User not found"));

                            // Pass UserDetails to generateToken
                            String token = jwtService.generateToken(userDetails);

                            // Create HTTP-only cookie
                            Cookie jwtCookie = new Cookie("jwt", token);
                            jwtCookie.setHttpOnly(true);
                            jwtCookie.setSecure(true);
                            jwtCookie.setPath("/");
                            jwtCookie.setMaxAge(2 * 24 * 60 * 60);

                            response.addCookie(jwtCookie);
                            response.sendRedirect("http://localhost:5173"); //"http://localhost:5173/oauth2/success?token="

                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");

                            Map<String, Object> responseData = new HashMap<>();
//                            responseData.put("token", token);
//                            responseData.put("email", email);
//                            responseData.put("name", oauth2User.getAttribute("name"));
//                            responseData.put("message", "OAuth2 login successful");

                            responseData.put("firstname", userEntity.getFirstName());
                            responseData.put("middlename", userEntity.getMiddleName());
                            responseData.put("lastname", userEntity.getLastName());
                            responseData.put("phonenumber", userEntity.getPhoneNumber());
                            responseData.put("email", email);
                            responseData.put("token", token);
                            responseData.put("message", "OAuth2 login successful");

                            String jsonResponse = new ObjectMapper().writeValueAsString(responseData);
                            response.getWriter().write(jsonResponse);
                        })
                        .failureHandler((request, response, exception) -> {
                            response.sendRedirect("http://localhost:5173/oauth2/error");

                            response.setContentType("application/json");
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());

                            Map<String, Object> errorData = new HashMap<>();
                            errorData.put("error", "OAuth2 login failed");
                            errorData.put("message", exception.getMessage());
                            errorData.put("details", exception.getClass().getSimpleName());

                            String jsonResponse = new ObjectMapper().writeValueAsString(errorData);
                            response.getWriter().write(jsonResponse);
                        })
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handling ->
                        handling.authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"" + authException.getMessage() + "\"}");
                        })
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    response.setContentType("application/json");
                                    response.getWriter().write("{\"error\": \"" + accessDeniedException.getMessage() + "\"}");
                                }))
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public ErrorController errorController() {
        return new ErrorController() {
            @RequestMapping("/error")
            public ResponseEntity<Map<String, String>> handleError(HttpServletRequest request) {
                Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

                if(status!= null) {
                    int statusCode = Integer.parseInt(status.toString());

                    if(statusCode == HttpStatus.NOT_FOUND.value()) {
                        return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(Map.of("error", "Resource not found"));
                    }
                }
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Internal Service Error"));
            }
        };
    }


//    @Bean
//    public AuthenticationProvider authenticationProvider(){
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(passwordEncoder());
//        provider.setUserDetailsService(userDetailsService());
//        return provider;
//    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "https://accounts.google.com",
                "http://localhost:8080",
                "http://localhost:3000",
                "https://webhook.stripe.com",
                "https://parttimer.vercel.app/"
        )); //
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
//                "Authorization",
//                "Content-Type",
//                "Accept",
//                "X-Requested-With",
//                "Origin",
//                "Access-Control-Request-Method",
//                "Access-Control-Request-Headers"
                "*"
        )); //Authorization", "Content-Type
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type")); // Allow the frontend to access this header
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        source.registerCorsConfiguration("/oauth2/**", configuration);
        source.registerCorsConfiguration("/login/oauth2/**", configuration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    @Lazy
    public MultiUserAuthenticationProvider multiUserAuthenticationProvider(
            UserRepository userRepository,
            LabourRepository labourRepository,
            PasswordEncoder passwordEncoder) {
        return new MultiUserAuthenticationProvider(userRepository, labourRepository, passwordEncoder);
    }

//    @Bean
//    public AuthenticationProvider multiUserAuthenticationProvider() {
//        return new MultiUserAuthenticationProvider();
//    }

//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = org.springframework.security.core.userdetails.User.builder()
//                .username("admin")
//                .password(passwordEncoder().encode("password"))
//                .roles("USER", "ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }
}
