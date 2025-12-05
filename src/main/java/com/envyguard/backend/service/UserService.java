package com.envyguard.backend.service;

import com.envyguard.backend.dto.UpdateUserRequest;
import com.envyguard.backend.dto.UserResponse;
import com.envyguard.backend.entity.User;
import com.envyguard.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (req.getEmail() != null && !req.getEmail().isBlank() && !req.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(req.getEmail())) {
                throw new IllegalArgumentException("El email ya está en uso");
            }
            user.setEmail(req.getEmail());
        }

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        Optional.ofNullable(req.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(req.getLastName()).ifPresent(user::setLastName);
        Optional.ofNullable(req.getRole()).ifPresent(user::setRole);
        Optional.ofNullable(req.getEnabled()).ifPresent(user::setEnabled);

        User saved = userRepository.save(user);

        return UserResponse.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .firstName(saved.getFirstName())
                .lastName(saved.getLastName())
                .role(saved.getRole())
                .enabled(saved.getEnabled())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        userRepository.delete(user);
    }
}
