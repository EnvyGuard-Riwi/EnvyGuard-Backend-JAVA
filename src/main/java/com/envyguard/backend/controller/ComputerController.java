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
 * 
 * @deprecated Use {@link PublicTestController} at /api/public/computers
 *             instead.
 *             This controller has security configuration issues in production.
 */
@Deprecated
@RestController
@RequestMapping("/computers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Computers Status (Deprecated)", description = "DEPRECATED - Use /api/public/computers instead")
public class ComputerController {

    private final ComputerRepository computerRepository;

    @Operation(summary = "List all monitored computers (DEPRECATED)", description = "DEPRECATED: Use GET /api/public/computers instead. This endpoint may return 403 due to security configuration issues.")
    @GetMapping
    public List<Computer> getAllComputers() {
        return computerRepository.findAll();
    }
}
