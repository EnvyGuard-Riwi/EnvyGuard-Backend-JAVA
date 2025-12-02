package com.envyguard.envyguard_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")  // Prefijo opcional para agrupar endpoints de prueba
public class TestController {
    
    // Endpoint 1: Simple test
    @GetMapping("/simple")
    public Map<String, String> simpleTest() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "¡Test simple funciona!");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return response;
    }
    
    // Endpoint 2: Health check alternativo
    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "EnvyGuard Backend");
        response.put("version", "1.0.0");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return response;
    }
    
    // Endpoint 3: Test sin seguridad
    @GetMapping("/public")
    public Map<String, String> publicTest() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Endpoint público sin autenticación");
        response.put("accessible", "true");
        return response;
    }
    
    // Endpoint 4: Test con info del sistema
    @GetMapping("/info")
    public Map<String, Object> systemInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("java.version", System.getProperty("java.version"));
        response.put("os.name", System.getProperty("os.name"));
        response.put("spring.boot.version", "3.2.0");
        response.put("application.name", "EnvyGuard Backend");
        return response;
    }
}
