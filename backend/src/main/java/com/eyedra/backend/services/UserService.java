package com.eyedra.backend.services;

import com.eyedra.backend.dto.UserDto;
import com.eyedra.backend.model.User;
import com.eyedra.backend.repository.UserRepository;
import com.eyedra.backend.util.PasswordUtil;
import com.eyedra.backend.dto.LoginRequest;
import com.eyedra.backend.dto.AuthResponse;
import com.eyedra.backend.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        logger.info("Registering user with email: {}", user.getEmail());
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.error("Registration failed. Email already exists: {}", user.getEmail());
            throw new IllegalArgumentException("A user with this email already exists");
        }

        user.setPassword(PasswordUtil.encryptPassword(user.getPassword()));

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    public User updateUser(String id, User user) {
        logger.info("Updating user with ID: {}", id);
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            logger.error("Update failed. User with ID: {} not found", id);
            throw new IllegalArgumentException("User not found");
        }

        user.setPassword(PasswordUtil.encryptPassword(user.getPassword()));
        user.setId(id); // Ensure the same user ID
        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully with ID: {}", updatedUser.getId());
        return updatedUser;
    }

    public User getUserById(String id) {
        logger.info("Fetching user with ID: {}", id);
        return userRepository.findById(id)

                .orElseThrow(() -> {
                    logger.error("Fetch failed. User with ID: {} not found", id);
                    return new IllegalArgumentException("User with ID " + id + " not found");
                });
    }

    public void deleteUser(String id) {
        logger.info("Deleting user with ID: {}", id);
        if (!userRepository.existsById(id)) {

            logger.error("Deletion failed. User with ID: {} not found", id);
            throw new IllegalArgumentException("User with ID " + id + " not found");
        }
        userRepository.deleteById(id);
        logger.info("User deleted successfully with ID: {}", id);
    }
    public AuthResponse login(LoginRequest loginRequest) {
        logger.info("User attempting login with email: {}", loginRequest.getEmail());
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty()) {
            logger.error("Login failed. Invalid email: {}", loginRequest.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = userOptional.get();
        if (!PasswordUtil.validatePassword(loginRequest.getPassword(), user.getPassword())) {
            logger.error("Login failed. Invalid password for email: {}", loginRequest.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }

        logger.info("Authentication successful for user with email: {}", user.getEmail());
        String token = TokenUtil.generateToken(user.getEmail(), 2629744000L);
        UserDto userDto = new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getMobile(), user.getRole().name());
        return new AuthResponse(token, userDto);
    }
}
