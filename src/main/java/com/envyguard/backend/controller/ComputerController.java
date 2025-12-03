package com.envyguard.backend.controller;

import com.envyguard.backend.entity.Computer;
import com.envyguard.backend.service.ComputerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for computer management.
 */
@RestController
@RequestMapping("/computers")
@RequiredArgsConstructor
public class ComputerController {

    private final ComputerService computerService;

    /**
     * Creates a new computer.
     * Endpoint: POST /api/computers
     *
     * @param request Map with name, ipAddress, macAddress, labName
     * @return Created computer
     */
    @PostMapping
    public ResponseEntity<Computer> createComputer(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String ipAddress = request.get("ipAddress");
        String macAddress = request.get("macAddress");
        String labName = request.get("labName");

        Computer computer = computerService.createComputer(name, ipAddress, macAddress, labName);
        return ResponseEntity.status(HttpStatus.CREATED).body(computer);
    }

    /**
     * Gets all computers.
     * Endpoint: GET /api/computers
     *
     * @return List of all computers
     */
    @GetMapping
    public ResponseEntity<List<Computer>> getAllComputers() {
        List<Computer> computers = computerService.getAllComputers();
        return ResponseEntity.ok(computers);
    }

    /**
     * Gets a computer by its name.
     * Endpoint: GET /api/computers/name/{name}
     *
     * @param name Computer name
     * @return Found computer
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Computer> getComputerByName(@PathVariable String name) {
        Computer computer = computerService.getComputerByName(name);
        return ResponseEntity.ok(computer);
    }

    /**
     * Gets a computer by its ID.
     * Endpoint: GET /api/computers/{id}
     *
     * @param id Computer ID
     * @return Found computer
     */
    @GetMapping("/{id}")
    public ResponseEntity<Computer> getComputerById(@PathVariable Long id) {
        Computer computer = computerService.getComputerById(id);
        return ResponseEntity.ok(computer);
    }

    /**
     * Updates a computer's status.
     * Endpoint: PUT /api/computers/{name}/status
     *
     * @param name Computer name
     * @param request Map with status
     * @return Updated computer
     */
    @PutMapping("/{name}/status")
    public ResponseEntity<Computer> updateComputerStatus(
            @PathVariable String name,
            @RequestBody Map<String, String> request) {
        String statusStr = request.get("status");
        if (statusStr == null || statusStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required and cannot be empty");
        }
        
        Computer.ComputerStatus status;
        try {
            status = Computer.ComputerStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + statusStr + 
                    ". Valid values are: ONLINE, OFFLINE, UNKNOWN");
        }
        
        Computer computer = computerService.updateComputerStatus(name, status);
        return ResponseEntity.ok(computer);
    }

    /**
     * Updates computer information.
     * Endpoint: PUT /api/computers/{id}
     *
     * @param id Computer ID
     * @param request Map with ipAddress, macAddress, labName
     * @return Updated computer
     */
    @PutMapping("/{id}")
    public ResponseEntity<Computer> updateComputer(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String ipAddress = request.get("ipAddress");
        String macAddress = request.get("macAddress");
        String labName = request.get("labName");

        Computer computer = computerService.updateComputer(id, ipAddress, macAddress, labName);
        return ResponseEntity.ok(computer);
    }

    /**
     * Deletes a computer.
     * Endpoint: DELETE /api/computers/{id}
     *
     * @param id Computer ID
     * @return Confirmation message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteComputer(@PathVariable Long id) {
        computerService.deleteComputer(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Computer deleted successfully");
        return ResponseEntity.ok(response);
    }
}

