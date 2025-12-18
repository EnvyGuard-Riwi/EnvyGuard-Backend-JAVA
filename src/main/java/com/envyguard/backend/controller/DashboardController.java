package com.envyguard.backend.controller;

import com.envyguard.backend.entity.Computer;
import com.envyguard.backend.repository.BlockedWebsiteRepository;
import com.envyguard.backend.repository.ComputerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for Dashboard statistics.
 * Provides aggregated data for the main dashboard cards.
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard statistics and metrics")
public class DashboardController {

    private final ComputerRepository computerRepository;
    private final BlockedWebsiteRepository blockedWebsiteRepository;

    /**
     * Helper endpoint to get all stats in one call.
     * Useful for initializing the dashboard.
     */
    @Operation(summary = "Get Dashboard Statistics", description = "Returns aggregated statistics for the dashboard cards: Total Computers, Online Computers, No Internet, Blocked Sites.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();

        // Card 1: Total Computadores
        stats.put("totalComputers", computerRepository.count());

        // Card 2: Computadores Prendidos (ONLINE)
        stats.put("onlineComputers", computerRepository.countByStatus(Computer.ComputerStatus.ONLINE));

        // Card 3: Computadores Sin Internet
        stats.put("noInternetComputers", computerRepository.countByHasInternet(false));

        // Card 4: Sitios Bloqueados
        stats.put("blockedWebsites", blockedWebsiteRepository.count());

        return ResponseEntity.ok(stats);
    }

    // Individual endpoints if the frontend prefers separate calls (matching user
    // request about generic "endpoints")

    @Operation(summary = "Get total computers count")
    @GetMapping("/stats/total-computers")
    public ResponseEntity<Long> getTotalComputers() {
        return ResponseEntity.ok(computerRepository.count());
    }

    @Operation(summary = "Get online computers count")
    @GetMapping("/stats/online-computers")
    public ResponseEntity<Long> getOnlineComputers() {
        return ResponseEntity.ok(computerRepository.countByStatus(Computer.ComputerStatus.ONLINE));
    }

    @Operation(summary = "Get no-internet computers count")
    @GetMapping("/stats/no-internet-computers")
    public ResponseEntity<Long> getNoInternetComputers() {
        return ResponseEntity.ok(computerRepository.countByHasInternet(false));
    }

    @Operation(summary = "Get blocked websites count")
    @GetMapping("/stats/blocked-websites")
    public ResponseEntity<Long> getBlockedWebsites() {
        return ResponseEntity.ok(blockedWebsiteRepository.count());
    }
}
