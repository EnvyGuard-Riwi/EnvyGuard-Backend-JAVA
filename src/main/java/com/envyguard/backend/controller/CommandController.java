package com.envyguard.backend.controller;

import com.envyguard.backend.dto.CommandRequest;
import com.envyguard.backend.entity.Command;
import com.envyguard.backend.service.CommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for remote command management.
 */
@RestController
@RequestMapping("/commands")
@RequiredArgsConstructor
public class CommandController {

    private final CommandService commandService;

    /**
     * Creates a new command for a computer.
     * Endpoint: POST /api/commands
     *
     * @param request CommandRequest with command data
     * @return Created command with PENDING status
     */
    @PostMapping
    public ResponseEntity<Command> createCommand(@Valid @RequestBody CommandRequest request) {
        Command command = commandService.createCommand(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
    }

    /**
     * Gets all commands for a specific computer.
     * Endpoint: GET /api/commands/computer/{computerName}
     *
     * @param computerName Computer name
     * @return List of commands
     */
    @GetMapping("/computer/{computerName}")
    public ResponseEntity<List<Command>> getCommandsByComputer(@PathVariable String computerName) {
        List<Command> commands = commandService.getCommandsByComputer(computerName);
        return ResponseEntity.ok(commands);
    }

    /**
     * Gets a command by its ID.
     * Endpoint: GET /api/commands/{id}
     *
     * @param id Command ID
     * @return Found command
     */
    @GetMapping("/{id}")
    public ResponseEntity<Command> getCommandById(@PathVariable Long id) {
        Command command = commandService.getCommandById(id);
        return ResponseEntity.ok(command);
    }

    /**
     * Gets all commands.
     * Endpoint: GET /api/commands
     *
     * @return List of all commands
     */
    @GetMapping
    public ResponseEntity<List<Command>> getAllCommands() {
        List<Command> commands = commandService.getAllCommands();
        return ResponseEntity.ok(commands);
    }

    /**
     * Gets all commands with a specific status.
     * Endpoint: GET /api/commands/status/{status}
     *
     * @param status Command status (PENDING, SENT, EXECUTED, FAILED)
     * @return List of commands
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Command>> getCommandsByStatus(@PathVariable Command.CommandStatus status) {
        List<Command> commands = commandService.getCommandsByStatus(status);
        return ResponseEntity.ok(commands);
    }

    /**
     * Endpoint for C# agent to report command execution result.
     * Endpoint: PUT /api/commands/{id}/status
     *
     * @param id Command ID
     * @param request Map with status and resultMessage
     * @return Updated command
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Command> updateCommandStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        Command.CommandStatus status = Command.CommandStatus.valueOf(request.get("status"));
        String resultMessage = request.get("resultMessage");
        
        Command command = commandService.updateCommandStatus(id, status, resultMessage);
        return ResponseEntity.ok(command);
    }
}
