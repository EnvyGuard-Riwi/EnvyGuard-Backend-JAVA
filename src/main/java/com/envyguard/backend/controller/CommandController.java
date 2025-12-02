package com.envyguard.backend.controller;

import com.envyguard.backend.dto.CommandRequest;
import com.envyguard.backend.entity.Command;
import com.envyguard.backend.service.CommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de comandos remotos.
 */
@RestController
@RequestMapping("/commands")
@RequiredArgsConstructor
public class CommandController {

    private final CommandService commandService;

    /**
     * Crea un nuevo comando para un computador.
     * Endpoint: POST /api/commands
     *
     * @param request CommandRequest con los datos del comando
     * @return Command creado con estado PENDING
     */
    @PostMapping
    public ResponseEntity<Command> createCommand(@Valid @RequestBody CommandRequest request) {
        Command command = commandService.createCommand(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
    }

    /**
     * Obtiene todos los comandos de un computador específico.
     * Endpoint: GET /api/commands/computer/{computerName}
     *
     * @param computerName Nombre del computador
     * @return Lista de comandos
     */
    @GetMapping("/computer/{computerName}")
    public ResponseEntity<List<Command>> getCommandsByComputer(@PathVariable String computerName) {
        List<Command> commands = commandService.getCommandsByComputer(computerName);
        return ResponseEntity.ok(commands);
    }

    /**
     * Obtiene un comando por su ID.
     * Endpoint: GET /api/commands/{id}
     *
     * @param id ID del comando
     * @return Command encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Command> getCommandById(@PathVariable Long id) {
        Command command = commandService.getCommandById(id);
        return ResponseEntity.ok(command);
    }

    /**
     * Obtiene todos los comandos con un estado específico.
     * Endpoint: GET /api/commands/status/{status}
     *
     * @param status Estado del comando (PENDING, SENT, EXECUTED, FAILED)
     * @return Lista de comandos
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Command>> getCommandsByStatus(@PathVariable Command.CommandStatus status) {
        List<Command> commands = commandService.getCommandsByStatus(status);
        return ResponseEntity.ok(commands);
    }

    /**
     * Endpoint para que el agente C# reporte el resultado de un comando.
     * Endpoint: PUT /api/commands/{id}/status
     *
     * @param id ID del comando
     * @param request Mapa con status y resultMessage
     * @return Command actualizado
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
