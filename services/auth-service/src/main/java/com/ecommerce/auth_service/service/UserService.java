package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User save(User user) {
        logger.info("Attempting to save user: {}", user.getEmail());
        User savedUser = userRepository.save(user);
        logger.info("User saved with ID: {}", savedUser.getId());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        logger.info("Searching for user with email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        logger.info("User found: {}", user.isPresent() ? user.get().getEmail() : "not found");
        return user;
    }
}
