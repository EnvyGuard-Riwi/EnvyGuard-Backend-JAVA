package com.envyguard.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Public test endpoint to verify security is not blocking.
 * This endpoint should be accessible without authentication.
 */
@RestController
@RequestMapping("/public")
public class PublicTestController {

    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of(
                "status", "ok",
                "message", "Public endpoint working!",
                "timestamp", java.time.LocalDateTime.now().toString());
    }

    @GetMapping("/computers-test")
    public Map<String, Object> computersTest() {
        return Map.of(
                "status", "ok",
                "message", "This is a test computers endpoint",
                "computers", java.util.List.of(
                        Map.of("id", 1, "name", "PC1", "status", "ONLINE"),
                        Map.of("id", 2, "name", "PC2", "status", "OFFLINE")));
    }
}
