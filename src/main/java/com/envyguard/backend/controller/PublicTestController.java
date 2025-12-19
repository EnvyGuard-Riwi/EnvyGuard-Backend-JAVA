package com.envyguard.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Public endpoints that don't require authentication.
 * Used for testing and public access to computer status.
 */
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
@Tag(name = "Public Endpoints", description = "Public API endpoints - no authentication required")
public class PublicTestController {

    private final com.envyguard.backend.repository.Sala4Repository sala4Repository;

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

    /**
     * List all monitored computers with their current status.
     * This is the main endpoint for the Radar/Dashboard.
     */
    @Operation(summary = "List all monitored computers", description = "Returns a list of computers with their current status (ONLINE/OFFLINE). Used by the Radar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of computers retrieved successfully")
    })
    @GetMapping("/computers")
    public List<com.envyguard.backend.entity.Sala4> getAllComputers() {
        return sala4Repository.findAll();
    }
}
