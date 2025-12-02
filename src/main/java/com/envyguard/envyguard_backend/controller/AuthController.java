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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
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
        System.out.println("🎯 LOGIN ENDPOINT HIT! Email: " + request.getEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
            
            System.out.println("✅ Authentication successful for: " + request.getEmail());
            
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userDetails.getUser();
            
            String jwtToken = jwtService.generateToken(userDetails);
            System.out.println("✅ JWT Token generated for: " + user.getEmail());
            
            LoginResponse response = new LoginResponse(
                jwtToken,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getId()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            System.out.println("❌ Bad credentials for: " + request.getEmail());
            throw new RuntimeException("Invalid email or password");
        } catch (AuthenticationException e) {
            System.out.println("❌ Authentication failed: " + e.getMessage());
            throw new RuntimeException("Authentication failed");
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        System.out.println("🎯 REGISTER ENDPOINT HIT! Email: " + request.getEmail());
        
        // Verificar si el usuario ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            System.out.println("❌ Email already registered: " + request.getEmail());
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
        System.out.println("✅ User created: " + user.getEmail());
        
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