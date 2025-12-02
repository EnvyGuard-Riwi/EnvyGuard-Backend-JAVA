package com.envyguard.envyguard_backend.controller;

import com.envyguard.envyguard_backend.dto.LoginRequest;
import com.envyguard.envyguard_backend.dto.LoginResponse;
import com.envyguard.envyguard_backend.dto.RegisterRequest;
import com.envyguard.envyguard_backend.entity.User;
import com.envyguard.envyguard_backend.repository.UserRepository;
import com.envyguard.envyguard_backend.security.JwtService;
import com.envyguard.envyguard_backend.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();
        
        String jwtToken = jwtService.generateToken(userDetails);
        
        LoginResponse response = new LoginResponse(
            jwtToken,
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getId()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        // Verificar si el usuario ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Crear nuevo usuario
        User user = new User(
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getFirstName(),
            request.getLastName()
        );
        
        userRepository.save(user);
        
        // Autenticar y generar token
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String jwtToken = jwtService.generateToken(userDetails);
        
        LoginResponse response = new LoginResponse(
            jwtToken,
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getId()
        );
        
        return ResponseEntity.ok(response);
    }
}
