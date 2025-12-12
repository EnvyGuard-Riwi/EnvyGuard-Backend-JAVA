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
@Tag(name = "Commands", description = """
    API para gestión de comandos remotos de laboratorios
    
    FLUJO DE TRABAJO:
    1. Frontend envía comando (POST /commands) con sala_number, pc_id y acción
    2. Backend busca el PC en la tabla sala_X y obtiene IP/MAC
    3. Backend crea registro en tabla 'commands' con estado PENDING
    4. Backend envía comando a RabbitMQ → estado cambia a SENT
    5. Agente C# recibe comando, lo ejecuta en el PC
    6. Agente reporta resultado (PUT /commands/{id}/status) → estado: EXECUTED o FAILED
    
    ACCIONES DISPONIBLES:
    - SHUTDOWN: Apagar equipo
    - REBOOT: Reiniciar equipo
    - WAKE_ON_LAN: Encender equipo (requiere MAC address)
    - LOCK_SESSION: Bloquear sesión del usuario
    - BLOCK_WEBSITE: Bloquear acceso a sitio web específico
    
    ESTADOS DE COMANDO:
    - PENDING: Creado, esperando ser enviado
    - SENT: Enviado a RabbitMQ exitosamente
    - EXECUTED: Ejecutado correctamente por el agente
    - FAILED: Falló en alguna etapa del proceso
    """)
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
            summary = "Crear y enviar comando remoto",
            description = """
                Crea un nuevo comando para ejecutar en un equipo de una sala específica.
                
                PROCESO:
                1. Se busca el PC en la tabla sala_{numero} por su ID
                2. Se obtiene automáticamente: nombre_pc, IP, MAC del PC
                3. Se crea el registro del comando con estado PENDING
                4. Se envía a RabbitMQ para que el agente C# lo ejecute
                5. El comando cambia a estado SENT
                
                EJEMPLO DE USO:
                Para apagar el PC 1 de la Sala 4:
                {
                  "sala_number": 4,
                  "pc_id": 1,
                  "action": "SHUTDOWN"
                }
                
                El sistema buscará en sala_4 el registro con id=1 y obtendrá automáticamente
                su IP (10.0.120.2) y MAC (08:bf:b8:03:13:0f) para enviar el comando.
                
                ACCIONES DISPONIBLES:
                - SHUTDOWN: Apaga el equipo inmediatamente
                - REBOOT: Reinicia el equipo
                - WAKE_ON_LAN: Enciende el equipo (solo si está apagado, requiere MAC)
                - LOCK_SESSION: Bloquea la sesión actual del usuario
                - BLOCK_WEBSITE: Bloquea acceso a un sitio (requiere URL en parameters)
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Comando creado y enviado exitosamente a RabbitMQ",
                    content = @Content(schema = @Schema(implementation = Command.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (sala o pc_id no válidos)"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "404", description = "PC no encontrado en la sala especificada")
    })
    @PostMapping
    public ResponseEntity<Command> createCommand(@Valid @RequestBody CommandRequest request) {
        Command command = commandService.createCommand(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
    }

    /**
     * Gets all commands for a specific computer by name.
     * Endpoint: GET /api/commands/computer/{computerName}
     *
     * @param computerName Computer name
     * @return List of commands
     */
    @Operation(
            summary = "Obtener historial de comandos de un PC",
            description = """
                Obtiene todos los comandos ejecutados en un equipo específico por su nombre.
                Útil para ver el historial completo de acciones realizadas en un PC.
                
                EJEMPLO: GET /api/commands/computer/PC 1
                
                Retorna todos los comandos (pendientes, ejecutados, fallidos) de ese PC.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comandos obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/computer/{computerName}")
    public ResponseEntity<List<Command>> getCommandsByComputer(
            @Parameter(description = "Nombre del PC (ej: 'PC 1', 'PC 2')", example = "PC 1")
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
            summary = "Obtener detalles de un comando",
            description = """
                Obtiene los detalles completos de un comando específico por su ID.
                Incluye información sobre:
                - Sala y PC objetivo
                - Acción ejecutada
                - Estado actual (PENDING, SENT, EXECUTED, FAILED)
                - Tiempos de creación, envío y ejecución
                - Mensaje de resultado del agente
                - Usuario que lo ejecutó
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comando encontrado con todos sus detalles",
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
            description = """
                    Obtiene la lista completa de todos los comandos registrados en el sistema, sin filtros.
                    
                    **Información retornada para cada comando:**
                    - ID del comando
                    - Sala y PC de destino (salaNumber, pcId, computerName, targetIp, macAddress)
                    - Acción ejecutada (SHUTDOWN, REBOOT, WAKE_ON_LAN, LOCK_SESSION, BLOCK_WEBSITE)
                    - Parámetros JSON opcionales
                    - Estado actual (PENDING, SENT, EXECUTED, FAILED)
                    - Timestamp de envío (sentAt) y ejecución (executedAt)
                    - Mensaje de resultado desde el agente C#
                    - Email del usuario que creó el comando
                    - Timestamp de creación
                    
                    **Útil para:**
                    - Panel de administración que muestra histórico completo
                    - Auditoría de todas las acciones realizadas
                    - Análisis y estadísticas de uso del sistema
                    
                    **Nota:** La lista puede ser extensa. Considera usar filtros por estado si solo necesitas comandos específicos.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comandos obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT inválido o ausente")
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
            description = """
                    Obtiene todos los comandos filtrados por un estado específico en el flujo de ejecución.
                    
                    **Estados disponibles:**
                    - **PENDING**: Comando creado pero aún no enviado a RabbitMQ (estado inicial)
                    - **SENT**: Comando enviado a la cola RabbitMQ, esperando que el agente C# lo procese
                    - **EXECUTED**: Comando ejecutado exitosamente por el agente C# en el PC destino
                    - **FAILED**: Error durante el envío o ejecución del comando
                    
                    **Ejemplos de uso:**
                    - `GET /api/commands/status/PENDING` - Ver comandos que aún no se enviaron
                    - `GET /api/commands/status/SENT` - Monitorear comandos en proceso (enviados pero no ejecutados)
                    - `GET /api/commands/status/EXECUTED` - Historial de comandos completados
                    - `GET /api/commands/status/FAILED` - Investigar comandos fallidos para retry o debugging
                    
                    **Información retornada:**
                    Misma estructura completa que GET /api/commands, pero filtrada por estado.
                    
                    **Útil para:**
                    - Dashboards que muestran comandos pendientes o en proceso
                    - Sistema de notificaciones para comandos fallidos
                    - Análisis de tasa de éxito/fallo de comandos
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comandos filtrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Estado inválido - Valores permitidos: PENDING, SENT, EXECUTED, FAILED"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT inválido o ausente")
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
            description = """
                    Actualiza el estado de un comando específico y opcionalmente agrega un mensaje de resultado.
                    
                    **Flujo de actualización de estados:**
                    1. **Frontend crea comando** → Estado inicial: `PENDING`
                    2. **Backend envía a RabbitMQ** → Actualiza a: `SENT`
                    3. **Agente C# ejecuta** → Actualiza a: `EXECUTED` (éxito) o `FAILED` (error)
                    
                    **Estados válidos:**
                    - `PENDING`: Comando creado pero no enviado
                    - `SENT`: Comando en cola RabbitMQ, esperando procesamiento
                    - `EXECUTED`: Comando completado exitosamente
                    - `FAILED`: Error en envío o ejecución
                    
                    **Request Body (JSON):**
                    ```json
                    {
                      "status": "EXECUTED",
                      "resultMessage": "PC apagado correctamente a las 14:30"
                    }
                    ```
                    
                    **Campos:**
                    - `status` (requerido): Nuevo estado del comando
                    - `resultMessage` (opcional): Mensaje descriptivo del resultado (éxito/error)
                    
                    **Usuarios principales:**
                    - **Agente C# en Windows**: Reporta resultado de ejecución con mensaje detallado
                    - **Backend (interno)**: Actualiza a SENT cuando envía el comando a RabbitMQ
                    - **Administrador (manual)**: Puede corregir estados manualmente si es necesario
                    
                    **Ejemplos de resultMessage:**
                    - Éxito: "Equipo apagado en 10 segundos", "WakeOnLAN enviado exitosamente"
                    - Error: "PC no responde al comando", "Privilegios insuficientes para ejecutar"
                    
                    **Response:** Retorna el comando completo actualizado con timestamp de ejecución.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente - Retorna comando completo",
                    content = @Content(schema = @Schema(implementation = Command.class))),
            @ApiResponse(responseCode = "400", description = "Estado inválido o formato JSON incorrecto - Valores permitidos: PENDING, SENT, EXECUTED, FAILED"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT inválido o ausente"),
            @ApiResponse(responseCode = "404", description = "Comando no encontrado - ID inválido")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Command> updateCommandStatus(
            @Parameter(description = "ID del comando a actualizar", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo estado y mensaje de resultado (opcional)",
                    content = @Content(
                            schema = @Schema(example = "{\"status\": \"EXECUTED\", \"resultMessage\": \"Comando ejecutado correctamente en PC 1 de Sala 4\"}")
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
