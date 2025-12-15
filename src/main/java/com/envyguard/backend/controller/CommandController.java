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
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Commands", description = "Remote command management API for controlling computers in different rooms")
public class CommandController {

        private final CommandService commandService;

        /**
         * Creates a new command for a computer.
         * Endpoint: POST /api/commands
         *
         * @param request CommandRequest with command data
         * @return Created command with PENDING status
         */
        @Operation(summary = "Create and send remote command", description = """
                        Creates a new command to execute on a computer in a specific room.

                        PROCESS:
                        1. Searches for the PC in sala_{number} table by its ID
                        2. Automatically retrieves: computer name, IP, MAC address
                        3. Creates command record with PENDING status
                        4. Sends to RabbitMQ for C# agent execution
                        5. Command status changes to SENT

                        EXAMPLE:
                        To shutdown PC 1 in Room 4:
                        {
                          "sala_number": 4,
                          "pc_id": 1,
                          "action": "SHUTDOWN"
                        }

                        The system will lookup sala_4 table for id=1 and automatically retrieve
                        its IP (10.0.120.2) and MAC (08:bf:b8:03:13:0f) to send the command.

                        AVAILABLE ACTIONS:
                        - SHUTDOWN: Shutdown computer immediately
                        - REBOOT: Restart computer
                        - WAKE_ON_LAN: Wake up computer (requires MAC address)
                        - LOCK_SESSION: Lock current user session
                        - BLOCK_WEBSITE: Block website access (requires URL in parameters)
                        - UNBLOCK_WEBSITE: Unblock website access
                        - FORMAT: Logical format/cleanup
                        - TEST: Test connection
                        - INSTALL_APP: Install application
                        - INSTALL_SNAP: Install snap package
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Command created and sent successfully to RabbitMQ", content = @Content(schema = @Schema(implementation = Command.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid data (invalid room or pc_id)"),
                        @ApiResponse(responseCode = "401", description = "User not authenticated"),
                        @ApiResponse(responseCode = "404", description = "PC not found in specified room")
        })
        @PostMapping
        public ResponseEntity<Command> createCommand(@Valid @RequestBody CommandRequest request) {
                Command command = commandService.createCommand(request);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
        }

        // ==========================================
        // ENDPOINTS DE PRUEBA (TEST ENDPOINTS)
        // ==========================================

        @Operation(summary = "Shutdown computer", description = "Sends SHUTDOWN command to turn off the target computer immediately.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Shutdown command sent successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid data"),
                        @ApiResponse(responseCode = "404", description = "PC not found")
        })
        @PostMapping("/shutdown")
        public ResponseEntity<Command> testShutdown(
                        @Parameter(description = "Room number (1-4)", example = "4") @RequestParam Integer salaNumber,
                        @Parameter(description = "PC ID", example = "1") @RequestParam Long pcId) {

                CommandRequest request = new CommandRequest(salaNumber, pcId, "SHUTDOWN", null);
                Command command = commandService.createCommand(request);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
        }

        @Operation(summary = "Reboot computer", description = "Sends REBOOT command to restart the target computer.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Reboot command sent successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid data"),
                        @ApiResponse(responseCode = "404", description = "PC not found")
        })
        @PostMapping("/reboot")
        public ResponseEntity<Command> testReboot(
                        @Parameter(description = "Room number (1-4)", example = "4") @RequestParam Integer salaNumber,
                        @Parameter(description = "PC ID", example = "1") @RequestParam Long pcId) {

                CommandRequest request = new CommandRequest(salaNumber, pcId, "REBOOT", null);
                Command command = commandService.createCommand(request);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
        }

        @Operation(summary = "Wake on LAN", description = "Sends Wake-on-LAN magic packet to turn on a powered-off computer using its MAC address.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Wake-on-LAN command sent successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid data"),
                        @ApiResponse(responseCode = "404", description = "PC not found")
        })
        @PostMapping("/wake-on-lan")
        public ResponseEntity<Command> testWakeOnLan(
                        @Parameter(description = "Room number (1-4)", example = "4") @RequestParam Integer salaNumber,
                        @Parameter(description = "PC ID", example = "1") @RequestParam Long pcId) {

                CommandRequest request = new CommandRequest(salaNumber, pcId, "WAKE_ON_LAN", null);
                Command command = commandService.createCommand(request);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
        }

        @Operation(summary = "Lock user session", description = "Locks the current user session on the target computer.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Lock session command sent successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid data"),
                        @ApiResponse(responseCode = "404", description = "PC not found")
        })
        @PostMapping("/lock-session")
        public ResponseEntity<Command> testLockSession(
                        @Parameter(description = "Room number (1-4)", example = "4") @RequestParam Integer salaNumber,
                        @Parameter(description = "PC ID", example = "1") @RequestParam Long pcId) {

                CommandRequest request = new CommandRequest(salaNumber, pcId, "LOCK_SESSION", null);
                Command command = commandService.createCommand(request);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
        }

        @Operation(summary = "Block website", description = "Blocks access to a specific website on the target computer by adding it to the hosts file.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Block website command sent successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid data"),
                        @ApiResponse(responseCode = "404", description = "PC not found")
        })
        @PostMapping("/block-website")
        public ResponseEntity<Command> testBlockWebsite(
                        @Parameter(description = "Room number (1-4)", example = "4") @RequestParam Integer salaNumber,
                        @Parameter(description = "PC ID", example = "1") @RequestParam Long pcId,
                        @Parameter(description = "Website URL to block (domain only)", example = "facebook.com") @RequestParam String url) {

                // El agente espera solo el dominio en parameters, no JSON
                CommandRequest request = new CommandRequest(salaNumber, pcId, "BLOCK_WEBSITE", url);
                Command command = commandService.createCommand(request);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
        }

        @Operation(summary = "Unblock website", description = "Removes website block by removing it from the hosts file on the target computer.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Unblock website command sent successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid data"),
                        @ApiResponse(responseCode = "404", description = "PC not found")
        })
        @PostMapping("/unblock-website")
        public ResponseEntity<Command> testUnblockWebsite(
                        @Parameter(description = "Room number (1-4)", example = "4") @RequestParam Integer salaNumber,
                        @Parameter(description = "PC ID", example = "1") @RequestParam Long pcId,
                        @Parameter(description = "Website URL to unblock (domain only)", example = "facebook.com") @RequestParam String url) {

                CommandRequest request = new CommandRequest(salaNumber, pcId, "UNBLOCK_WEBSITE", url);
                Command command = commandService.createCommand(request);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
        }

        @Operation(summary = "Logical format/cleanup", description = "Performs logical format or system cleanup on the target computer (removes temp files, cache, etc).")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Format command sent successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid data"),
                        @ApiResponse(responseCode = "404", description = "PC not found")
        })
        @PostMapping("/format")
        public ResponseEntity<Command> testFormat(
                        @Parameter(description = "Room number (1-4)", example = "4") @RequestParam Integer salaNumber,
                        @Parameter(description = "PC ID", example = "1") @RequestParam Long pcId) {

                CommandRequest request = new CommandRequest(salaNumber, pcId, "FORMAT", null);
                Command command = commandService.createCommand(request);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
        }

        @Operation(summary = "Test connection", description = "Tests connectivity with the agent running on the target computer.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Test command sent successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid data"),
                        @ApiResponse(responseCode = "404", description = "PC not found")
        })
        @PostMapping("/test")
        public ResponseEntity<Command> testConnection(
                        @Parameter(description = "Room number (1-4)", example = "4") @RequestParam Integer salaNumber,
                        @Parameter(description = "PC ID", example = "1") @RequestParam Long pcId) {

                CommandRequest request = new CommandRequest(salaNumber, pcId, "TEST", null);
                Command command = commandService.createCommand(request);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
        }

        @Operation(summary = "Install application", description = "Installs a standard application package on the target computer using system package manager.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Install application command sent successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid data"),
                        @ApiResponse(responseCode = "404", description = "PC not found")
        })
        @PostMapping("/install-app")
        public ResponseEntity<Command> testInstallApp(
                        @Parameter(description = "Room number (1-4)", example = "4") @RequestParam Integer salaNumber,
                        @Parameter(description = "PC ID", example = "1") @RequestParam Long pcId,
                        @Parameter(description = "Package name to install", example = "git") @RequestParam String packageName) {

                CommandRequest request = new CommandRequest(salaNumber, pcId, "INSTALL_APP", packageName);
                Command command = commandService.createCommand(request);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
        }

        @Operation(summary = "Install snap package", description = "Installs a snap package on the target computer using snap package manager.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "202", description = "Install snap command sent successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid data"),
                        @ApiResponse(responseCode = "404", description = "PC not found")
        })
        @PostMapping("/install-snap")
        public ResponseEntity<Command> testInstallSnap(
                        @Parameter(description = "Room number (1-4)", example = "4") @RequestParam Integer salaNumber,
                        @Parameter(description = "PC ID", example = "1") @RequestParam Long pcId,
                        @Parameter(description = "Snap package name to install", example = "rider") @RequestParam String snapName) {

                CommandRequest request = new CommandRequest(salaNumber, pcId, "INSTALL_SNAP", snapName);
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
        @Operation(summary = "Get command history by computer", description = """
                        Retrieves all commands executed on a specific computer by its name.
                        Useful for viewing complete action history on a PC.

                        EXAMPLE: GET /api/commands/computer/PC 1

                        Returns all commands (pending, executed, failed) for that PC.
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Command list retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "Not authenticated")
        })
        @GetMapping("/computer/{computerName}")
        public ResponseEntity<List<Command>> getCommandsByComputer(
                        @Parameter(description = "Computer name (e.g., 'PC 1', 'PC 2')", example = "PC 1") @PathVariable String computerName) {
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
        @Operation(summary = "Get command details by ID", description = """
                        Retrieves complete details of a specific command by its ID.
                        Includes information about:
                        - Target room and PC
                        - Executed action
                        - Current status (PENDING, SENT, EXECUTED, FAILED)
                        - Creation, send, and execution timestamps
                        - Agent result message
                        - User who executed it
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Command found with all details", content = @Content(schema = @Schema(implementation = Command.class))),
                        @ApiResponse(responseCode = "401", description = "Not authenticated"),
                        @ApiResponse(responseCode = "404", description = "Command not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<Command> getCommandById(
                        @Parameter(description = "Command ID", example = "1") @PathVariable Long id) {
                Command command = commandService.getCommandById(id);
                return ResponseEntity.ok(command);
        }

        /**
         * Gets all commands.
         * Endpoint: GET /api/commands
         *
         * @return List of all commands
         */
        @Operation(summary = "Get all commands", description = """
                        Retrieves complete list of all commands registered in the system without filters.

                        **Information returned for each command:**
                        - Command ID
                        - Target room and PC (salaNumber, pcId, computerName, targetIp, macAddress)
                        - Executed action (SHUTDOWN, REBOOT, WAKE_ON_LAN, LOCK_SESSION, BLOCK_WEBSITE, etc.)
                        - Optional JSON parameters
                        - Current status (PENDING, SENT, EXECUTED, FAILED)
                        - Send (sentAt) and execution (executedAt) timestamps
                        - Result message from C# agent
                        - Email of user who created the command
                        - Creation timestamp

                        **Useful for:**
                        - Admin panel showing complete history
                        - Audit of all performed actions
                        - System usage analysis and statistics

                        **Note:** List can be extensive. Consider using status filters if you only need specific commands.
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Command list retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "Not authenticated - Invalid or missing JWT token")
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
        @Operation(summary = "Get commands by status", description = """
                        Retrieves all commands filtered by a specific status in the execution flow.

                        **Available statuses:**
                        - **PENDING**: Command created but not yet sent to RabbitMQ (initial state)
                        - **SENT**: Command sent to RabbitMQ queue, waiting for C# agent to process
                        - **EXECUTED**: Command executed successfully by C# agent on target PC
                        - **FAILED**: Error during sending or execution of command

                        **Usage examples:**
                        - `GET /api/commands/status/PENDING` - View commands not yet sent
                        - `GET /api/commands/status/SENT` - Monitor commands in process (sent but not executed)
                        - `GET /api/commands/status/EXECUTED` - History of completed commands
                        - `GET /api/commands/status/FAILED` - Investigate failed commands for retry or debugging

                        **Returned information:**
                        Same complete structure as GET /api/commands, but filtered by status.

                        **Useful for:**
                        - Dashboards showing pending or in-process commands
                        - Notification system for failed commands
                        - Analysis of command success/failure rate
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Command list filtered successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid status - Allowed values: PENDING, SENT, EXECUTED, FAILED"),
                        @ApiResponse(responseCode = "401", description = "Not authenticated - Invalid or missing JWT token")
        })
        @GetMapping("/status/{status}")
        public ResponseEntity<List<Command>> getCommandsByStatus(
                        @Parameter(description = "Command status", example = "EXECUTED") @PathVariable Command.CommandStatus status) {
                List<Command> commands = commandService.getCommandsByStatus(status);
                return ResponseEntity.ok(commands);
        }

        /**
         * Endpoint for C# agent to report command execution result.
         * Endpoint: PUT /api/commands/{id}/status
         *
         * @param id      Command ID
         * @param request Map with status and resultMessage
         * @return Updated command
         */
        @Operation(summary = "Update command status", description = """
                        Updates the status of a specific command and optionally adds a result message.

                        **Status update flow:**
                        1. **Frontend creates command** → Initial status: `PENDING`
                        2. **Backend sends to RabbitMQ** → Updates to: `SENT`
                        3. **C# Agent executes** → Updates to: `EXECUTED` (success) or `FAILED` (error)

                        **Valid statuses:**
                        - `PENDING`: Command created but not sent
                        - `SENT`: Command in RabbitMQ queue, waiting for processing
                        - `EXECUTED`: Command completed successfully
                        - `FAILED`: Error in sending or execution

                        **Request Body (JSON):**
                        ```json
                        {
                          "status": "EXECUTED",
                          "resultMessage": "PC shutdown successfully at 14:30"
                        }
                        ```

                        **Fields:**
                        - `status` (required): New command status
                        - `resultMessage` (optional): Descriptive result message (success/error)

                        **Main users:**
                        - **C# Agent on Windows**: Reports execution result with detailed message
                        - **Backend (internal)**: Updates to SENT when sending command to RabbitMQ
                        - **Administrator (manual)**: Can manually correct statuses if necessary

                        **Example resultMessage:**
                        - Success: "Computer shutdown in 10 seconds", "WakeOnLAN sent successfully"
                        - Error: "PC does not respond to command", "Insufficient privileges to execute"

                        **Response:** Returns complete updated command with execution timestamp.
                        """)
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Status updated successfully - Returns complete command", content = @Content(schema = @Schema(implementation = Command.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid status or incorrect JSON format - Allowed values: PENDING, SENT, EXECUTED, FAILED"),
                        @ApiResponse(responseCode = "401", description = "Not authenticated - Invalid or missing JWT token"),
                        @ApiResponse(responseCode = "404", description = "Command not found - Invalid ID")
        })
        @PutMapping("/{id}/status")
        public ResponseEntity<Command> updateCommandStatus(
                        @Parameter(description = "Command ID to update", example = "1") @PathVariable Long id,
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "New status and result message (optional)", content = @Content(schema = @Schema(example = "{\"status\": \"EXECUTED\", \"resultMessage\": \"Command executed successfully on PC 1 in Room 4\"}"))) @RequestBody Map<String, String> request) {
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
