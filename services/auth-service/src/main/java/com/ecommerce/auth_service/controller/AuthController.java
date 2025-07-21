package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API for user authentication")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
//    private final UserService userService;
//    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AuthController(PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and publishes a 'user.registered' event to Kafka")
    public ResponseEntity<String> register(@RequestBody User user) {
        // Check unique user

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user

        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        // Kafka event

        logger.info("User registered - {}", user.getEmail());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT token")
    public ResponseEntity<String> login(@RequestBody User user) {
        // Check unique user and check password

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        logger.info("User logged in - {}", user.getEmail());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate JWT token", description = "Validates the provided JWT token")
    public ResponseEntity<Boolean> validate(@RequestParam String token) {
        boolean isValid = jwtService.isValidateToken(token);
        logger.info("Token validation result for {}: {}", token, isValid);
        return ResponseEntity.ok(isValid);
    }
}
