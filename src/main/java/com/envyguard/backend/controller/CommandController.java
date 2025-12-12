package com.envyguard.backend.controller;

import com.envyguard.backend.dto.CommandRequest;
import com.envyguard.backend.entity.Command;
import com.envyguard.backend.service.CommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Commands", description = "API para gestión de comandos remotos (encender, apagar, reiniciar equipos)")
@SecurityRequirement(name = "Bearer Authentication")
public class CommandController {

    private final CommandService commandService;

    /**
     * Creates a new command for a computer.
     * Endpoint: POST /api/commands
     *
     * @param request CommandRequest with command data
     * @return Created command with PENDING status
     */
    @Operation(
            summary = "Crear comando remoto",
            description = "Crea un nuevo comando para ejecutar en un equipo remoto. " +
                         "Acciones disponibles: SHUTDOWN (apagar), REBOOT (reiniciar), WAKE_ON_LAN (encender), " +
                         "LOCK_SESSION (bloquear sesión), BLOCK_WEBSITE (bloquear sitio web)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Comando creado y enviado exitosamente",
                    content = @Content(schema = @Schema(implementation = Command.class))),
            @ApiResponse(responseCode = "400", description = "Datos de comando inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Equipo no encontrado")
    })
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
    @Operation(
            summary = "Obtener comandos por equipo",
            description = "Obtiene todos los comandos ejecutados en un equipo específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comandos obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/computer/{computerName}")
    public ResponseEntity<List<Command>> getCommandsByComputer(
            @Parameter(description = "Nombre del equipo", example = "PC-LAB-01")
            @PathVariable String computerName) {
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
    @Operation(
            summary = "Obtener comando por ID",
            description = "Obtiene los detalles de un comando específico por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comando encontrado",
                    content = @Content(schema = @Schema(implementation = Command.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Comando no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Command> getCommandById(
            @Parameter(description = "ID del comando", example = "1")
            @PathVariable Long id) {
        Command command = commandService.getCommandById(id);
        return ResponseEntity.ok(command);
    }

    /**
     * Gets all commands.
     * Endpoint: GET /api/commands
     *
     * @return List of all commands
     */
    @Operation(
            summary = "Obtener todos los comandos",
            description = "Obtiene la lista completa de todos los comandos registrados en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comandos obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
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
    @Operation(
            summary = "Obtener comandos por estado",
            description = "Obtiene todos los comandos filtrados por estado. " +
                         "Estados disponibles: PENDING (pendiente), SENT (enviado), EXECUTED (ejecutado), FAILED (fallido)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comandos obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Estado inválido"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Command>> getCommandsByStatus(
            @Parameter(description = "Estado del comando", example = "EXECUTED")
            @PathVariable Command.CommandStatus status) {
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
    @Operation(
            summary = "Actualizar estado de comando",
            description = "Actualiza el estado de un comando. Usado principalmente por el agente C# para reportar " +
                         "el resultado de la ejecución del comando. Estados: PENDING, SENT, EXECUTED, FAILED"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = Command.class))),
            @ApiResponse(responseCode = "400", description = "Estado inválido o datos incorrectos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Comando no encontrado")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Command> updateCommandStatus(
            @Parameter(description = "ID del comando", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo estado y mensaje de resultado",
                    content = @Content(
                            schema = @Schema(example = "{\"status\": \"EXECUTED\", \"resultMessage\": \"Comando ejecutado correctamente\"}")
                    )
            )
            @RequestBody Map<String, String> request) {
        String statusStr = request.get("status");
        if (statusStr == null || statusStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required and cannot be empty");
        }
        
        Command.CommandStatus status;
        try {
            status = Command.CommandStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + statusStr + 
                    ". Valid values are: PENDING, SENT, EXECUTED, FAILED");
        }
        
        String resultMessage = request.get("resultMessage");
        
        Command command = commandService.updateCommandStatus(id, status, resultMessage);
        return ResponseEntity.ok(command);
    }
}
