package com.envyguard.backend.controller;

import com.envyguard.backend.dto.LoginRequest;
import com.envyguard.backend.dto.LoginResponse;
import com.envyguard.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para autenticación y registro de usuarios.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint para autenticación de usuarios.
     *
     * @param request LoginRequest con email y password
     * @return LoginResponse con token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para registro de nuevos usuarios.
     *
     * @param request Mapa con email, password, firstName, lastName
     * @return Mensaje de éxito
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String firstName = request.get("firstName");
        String lastName = request.get("lastName");

        authService.register(email, password, firstName, lastName);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint de salud para verificar que el servicio está activo.
     *
     * @return Estado del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "auth");
        return ResponseEntity.ok(response);
    }
}
