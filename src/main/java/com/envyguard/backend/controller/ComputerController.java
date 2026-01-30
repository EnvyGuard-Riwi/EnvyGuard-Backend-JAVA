package com.envyguard.backend.controller;

import com.envyguard.backend.entity.Computer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    private final com.envyguard.backend.repository.Sala4Repository sala4Repository;

    @Operation(summary = "List all monitored computers (DEPRECATED)", description = "DEPRECATED: Use GET /api/public/computers instead. This endpoint may return 403 due to security configuration issues.")
    @GetMapping
    public List<Computer> getAllComputers() {
        return sala4Repository.findAll().stream().map(sala4 -> {
            return Computer.builder()
                    .id(sala4.getId())
                    .name(sala4.getNombrePc())
                    .ipAddress(sala4.getIp())
                    .macAddress(sala4.getMac())
                    .status(sala4.getStatus() != null ? sala4.getStatus() : Computer.ComputerStatus.OFFLINE)
                    .lastSeen(sala4.getLastSeen())
                    .roomNumber(4)
                    .labName("Sala 4")
                    .build();
        }).collect(java.util.stream.Collectors.toList());
    }
}
