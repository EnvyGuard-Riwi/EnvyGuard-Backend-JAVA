package com.envyguard.backend.service;

import com.envyguard.backend.dto.LoginRequest;
import com.envyguard.backend.dto.LoginResponse;
import com.envyguard.backend.dto.UserResponse;
import com.envyguard.backend.entity.Role;
import com.envyguard.backend.entity.User;
import com.envyguard.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling authentication and user registration.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    /**
     * Registers a new user.
     *
     * @param email     User email
     * @param password  User password
     * @param firstName User first name
     * @param lastName  User last name
     * @param role      User role
     * @return The registered User
     */
    @Transactional
    public User register(String email, String password, String firstName, String lastName, Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role != null ? role : Role.OPERATOR);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        try {
            emailService.sendCredentials(email, email, password);
        } catch (Exception e) {
            log.error("Error sending email: " + e.getMessage(), e);
        }

        return savedUser;
    }

    /**
     * Authenticates a user.
     *
     * @param request Login request containing email and password
     * @return Login response with JWT token and user details
     */
    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());

        return response;
    }

    /**
     * Retrieves all users.
     *
     * @return List of all users
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserResponse response = new UserResponse();
                    response.setId(user.getId());
                    response.setEmail(user.getEmail());
                    response.setFirstName(user.getFirstName());
                    response.setLastName(user.getLastName());
                    response.setRole(user.getRole());
                    response.setEnabled(user.isEnabled());
                    return response;
                })
                .collect(Collectors.toList());
    }
}