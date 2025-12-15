package com.envyguard.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * REST controller for computer management.
 * 
 * ⚠️ DEPRECATED: This controller is being replaced by the new room-based architecture.
 * 
 * NEW STRUCTURE:
 * - Computers are no longer in a generic 'computers' table
 * - Now organized by rooms: sala_1, sala_2, sala_3, sala_4
 * - Each PC has a unique ID within its room
 * 
 * UPDATED ENDPOINTS:
 * - To send commands, use: POST /api/commands
 *   Example: {"salaNumber": 4, "pcId": 1, "action": "SHUTDOWN", "parameters": ""}
 * 
 * - To view commands: GET /api/commands
 * - To view commands by status: GET /api/commands/status/{status}
 * - To view specific command: GET /api/commands/{id}
 * 
 * TODO: 
 * - Create SalaController to manage rooms and list PCs by room
 * - Remove ComputerController and ComputerService when frontend is migrated
 */
@RestController
@RequestMapping("/computers")
@RequiredArgsConstructor
@Tag(name = "Computers (Deprecated)", description = "Computer management API - DEPRECATED, use Commands API instead")
public class ComputerController {

    /**
     * Migration information endpoint.
     */
    @Operation(summary = "Migration information", description = "Provides information about migration to new room-based architecture.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Migration information retrieved")
    })
    @GetMapping("/migration-info")
    public ResponseEntity<Map<String, String>> getMigrationInfo() {
        return ResponseEntity.ok(Map.of(
            "status", "DEPRECATED",
            "message", "Este endpoint está deprecado. Por favor usa /api/commands para enviar comandos.",
            "newEndpoint", "POST /api/commands",
            "example", "{\"salaNumber\": 4, \"pcId\": 1, \"action\": \"SHUTDOWN\"}"
        ));
    }
}
