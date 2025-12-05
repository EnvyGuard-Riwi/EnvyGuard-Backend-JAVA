package com.envyguard.backend.controller;

import com.envyguard.backend.dto.UpdateUserRequest;
import com.envyguard.backend.dto.UserResponse;
import com.envyguard.backend.entity.User;
import com.envyguard.backend.repository.UserRepository;
import com.envyguard.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para operaciones de usuarios.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest req) {
        // Security: only allow the owner (same email) to update their data. If you need admin, extend later.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autenticado");
        }

        String requesterEmail = null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails) {
            requesterEmail = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            requesterEmail = (String) principal;
        }

        User target = userRepository.findById(id).orElse(null);
        if (target == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        if (requesterEmail == null || !requesterEmail.equals(target.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado para actualizar este usuario");
        }

        try {
            UserResponse updated = userService.updateUser(id, req);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
