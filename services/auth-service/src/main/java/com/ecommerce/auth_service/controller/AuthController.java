package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.dto.LoginRequest;
import com.ecommerce.auth_service.dto.UserDto;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.service.JwtService;
import com.ecommerce.auth_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API for user authentication")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AuthController(PasswordEncoder passwordEncoder, JwtService jwtService, UserService userService, KafkaTemplate kafkaTemplate) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userService = userService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and publishes a 'user.registered' event to Kafka")
    public ResponseEntity<String> register(@RequestBody User user) {
        // Check unique user
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("Registration failed: User with email {} already exists", user.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User with this email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser;
        try {
            savedUser = userService.save(user);
            logger.info("User saved successfully: {}", savedUser.getEmail());
        } catch (Exception e) {
            logger.error("Failed to save user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save user: " + e.getMessage());
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        // Kafka event
        try {
            kafkaTemplate.send("user.registered", new UserDto(savedUser.getId(), savedUser.getEmail(), savedUser.getRole()));
            logger.info("User registered and event published - {}", savedUser.getEmail());
        } catch (Exception e) {
            logger.error("Failed to publish Kafka event for user {}: {}", savedUser.getEmail(), e.getMessage());
        }

        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT token")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        // Check username and check password
        User user = userService.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login failed: User with email {} not found", loginRequest.getEmail());
                    return new RuntimeException("User not found");
                });

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.warn("Login failed: User with email {} not found", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

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
