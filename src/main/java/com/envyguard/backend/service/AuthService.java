package com.envyguard.backend.service;

import com.envyguard.backend.dto.LoginRequest;
import com.envyguard.backend.dto.LoginResponse;
import com.envyguard.backend.dto.UserResponse;
import com.envyguard.backend.entity.User;
import com.envyguard.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for handling user authentication and registration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    /**
     * Registers a new user in the system and sends welcome email with credentials.
     *
     * @param email User email
     * @param password Unencrypted password
     * @param firstName User first name
     * @param lastName User last name
     * @return Created user
     * @throws IllegalArgumentException If email already exists
     */
    @Transactional
    public User register(String email, String password, String firstName, String lastName) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        
        // Send welcome email with credentials
        try {
            emailService.sendWelcomeEmail(email, firstName, email, password);
            log.info("Welcome email sent to user: {}", email);
        } catch (Exception e) {
            log.error("Failed to send welcome email to user {}: {}", email, e.getMessage());
            // Note: We don't throw exception here to avoid breaking registration process
        }

        return savedUser;
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param request Login request with email and password
     * @return LoginResponse with JWT token and user data
     * @throws org.springframework.security.core.AuthenticationException If credentials are invalid
     */
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String jwtToken = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    /**
     * Retrieves all users from the system.
     *
     * @return List of UserResponse objects containing user information
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .enabled(user.getEnabled())
                        .createdAt(user.getCreatedAt())
                        .build())
                .toList();
    }
}
