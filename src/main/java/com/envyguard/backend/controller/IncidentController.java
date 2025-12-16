package com.envyguard.backend.controller;

import com.envyguard.backend.dto.IncidentRequest;
import com.envyguard.backend.dto.IncidentResponse;
import com.envyguard.backend.entity.IncidentStatus;
import com.envyguard.backend.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing incidents.
 */
@RestController
@RequestMapping("/incidents")
@RequiredArgsConstructor
@Tag(name = "Incidents", description = "Management of Incidents/News")
public class IncidentController {

    private final IncidentService incidentService;

    @Operation(summary = "Create a new incident", description = "Creates a new incident with PENDING status.")
    @PostMapping
    public ResponseEntity<IncidentResponse> create(@Valid @RequestBody IncidentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incidentService.create(request));
    }

    @Operation(summary = "List incidents by status", description = "Returns a list of incidents filtered by status (PENDING/COMPLETED).")
    @GetMapping
    public ResponseEntity<List<IncidentResponse>> findAllByStatus(@RequestParam IncidentStatus status) {
        return ResponseEntity.ok(incidentService.findAllByStatus(status));
    }

    @Operation(summary = "List all incidents", description = "Returns a list of all incidents without filtering.")
    @GetMapping("/all")
    public ResponseEntity<List<IncidentResponse>> findAll() {
        return ResponseEntity.ok(incidentService.findAll());
    }

    @Operation(summary = "Mark incident as completed", description = "Updates the status of an incident to COMPLETED.")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<IncidentResponse> markAsCompleted(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.markAsCompleted(id));
    }
}
