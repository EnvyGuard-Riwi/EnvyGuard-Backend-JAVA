package com.envyguard.backend.controller;

import com.envyguard.backend.entity.Computer;
import com.envyguard.backend.repository.ComputerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for computer monitoring.
 * Provides endpoints for the Radar/Dashboard to discover and list computers
 * status.
 */
@RestController
@RequestMapping("/computers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Global CORS for this controller
@Tag(name = "Computers Status", description = "Real-time status monitoring API")
public class ComputerController {

    private final ComputerRepository computerRepository;

    @Operation(summary = "List all monitored computers", description = "Returns a list of computers with their current status (ONLINE/OFFLINE). Used by the Radar.")
    @GetMapping
    public List<Computer> getAllComputers() {
        return computerRepository.findAll();
    }

    // Keep migration info if needed, or remove. I'll remove it to clean up as they
    // seem to move past it.
}
