package com.envyguard.backend.controller;

import com.envyguard.backend.dto.CommandRequest;
import com.envyguard.backend.entity.Command;
import com.envyguard.backend.entity.Computer;
import com.envyguard.backend.service.CommandService;
import com.envyguard.backend.service.ComputerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Computer Management", description = "Endpoints para gestión de computadoras y comandos remotos")
public class ComputerController {

    private final ComputerService computerService;
    private final CommandService commandService;

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

    // ========== COMPUTER REMOTE COMMANDS ==========

    /**
     * Apagar computadora (Shutdown).
     * Endpoint: POST /api/computers/{id}/shutdown
     *
     * @param id Computer ID
     * @return Command created
     */
    @PostMapping("/{id}/shutdown")
    @Operation(summary = "Apagar computadora", description = "Envía comando para apagar la computadora de forma remota")
    public ResponseEntity<Command> shutdownComputer(@PathVariable Long id) {
        Computer computer = computerService.getComputerById(id);
        
        CommandRequest request = new CommandRequest();
        request.setComputerName(computer.getName());
        request.setAction("shutdown");
        request.setTargetIp(computer.getIpAddress());
        request.setParameters("");
        
        Command command = commandService.createCommand(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
    }

    /**
     * Reiniciar computadora (Reboot).
     * Endpoint: POST /api/computers/{id}/reboot
     *
     * @param id Computer ID
     * @return Command created
     */
    @PostMapping("/{id}/reboot")
    @Operation(summary = "Reiniciar computadora", description = "Envía comando para reiniciar la computadora")
    public ResponseEntity<Command> rebootComputer(@PathVariable Long id) {
        Computer computer = computerService.getComputerById(id);
        
        CommandRequest request = new CommandRequest();
        request.setComputerName(computer.getName());
        request.setAction("reboot");
        request.setTargetIp(computer.getIpAddress());
        request.setParameters("");
        
        Command command = commandService.createCommand(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
    }

    /**
     * Encender computadora (Wake on LAN).
     * Endpoint: POST /api/computers/{id}/wakeup
     *
     * @param id Computer ID
     * @return Command created
     */
    @PostMapping("/{id}/wakeup")
    @Operation(summary = "Encender computadora (WOL)", description = "Envía magic packet Wake-on-LAN para encender la computadora")
    public ResponseEntity<Command> wakeupComputer(@PathVariable Long id) {
        Computer computer = computerService.getComputerById(id);
        
        if (computer.getMacAddress() == null || computer.getMacAddress().isEmpty()) {
            throw new IllegalArgumentException("Computer does not have MAC address configured");
        }
        
        CommandRequest request = new CommandRequest();
        request.setComputerName(computer.getName());
        request.setAction("wakeup");
        request.setTargetIp("");
        request.setMacAddress(computer.getMacAddress());
        request.setParameters("");
        
        Command command = commandService.createCommand(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
    }

    /**
     * Bloquear sitios web.
     * Endpoint: POST /api/computers/{id}/block-sites
     *
     * @param id Computer ID
     * @param body Map with "sites" parameter (comma-separated list)
     * @return Command created
     */
    @PostMapping("/{id}/block-sites")
    @Operation(summary = "Bloquear sitios web", description = "Bloquea el acceso a sitios web específicos (ej: facebook.com,youtube.com)")
    public ResponseEntity<Command> blockSites(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Computer computer = computerService.getComputerById(id);
        
        String sites = body.get("sites");
        if (sites == null || sites.isEmpty()) {
            throw new IllegalArgumentException("Sites parameter is required (e.g., 'facebook.com,youtube.com')");
        }
        
        CommandRequest request = new CommandRequest();
        request.setComputerName(computer.getName());
        request.setAction("block_sites");
        request.setTargetIp(computer.getIpAddress());
        request.setParameters(sites);
        
        Command command = commandService.createCommand(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
    }

    /**
     * Desbloquear sitios web.
     * Endpoint: POST /api/computers/{id}/unblock-sites
     *
     * @param id Computer ID
     * @param body Map with "sites" parameter (comma-separated list)
     * @return Command created
     */
    @PostMapping("/{id}/unblock-sites")
    @Operation(summary = "Desbloquear sitios web", description = "Desbloquea el acceso a sitios web previamente bloqueados")
    public ResponseEntity<Command> unblockSites(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Computer computer = computerService.getComputerById(id);
        
        String sites = body.get("sites");
        if (sites == null || sites.isEmpty()) {
            throw new IllegalArgumentException("Sites parameter is required (e.g., 'facebook.com,youtube.com')");
        }
        
        CommandRequest request = new CommandRequest();
        request.setComputerName(computer.getName());
        request.setAction("unblock_sites");
        request.setTargetIp(computer.getIpAddress());
        request.setParameters(sites);
        
        Command command = commandService.createCommand(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
    }

    /**
     * Instalar aplicación.
     * Endpoint: POST /api/computers/{id}/install-app
     *
     * @param id Computer ID
     * @param body Map with "appName" parameter
     * @return Command created
     */
    @PostMapping("/{id}/install-app")
    @Operation(summary = "Instalar aplicación", description = "Instala una aplicación en la computadora remota")
    public ResponseEntity<Command> installApp(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Computer computer = computerService.getComputerById(id);
        
        String appName = body.get("appName");
        if (appName == null || appName.isEmpty()) {
            throw new IllegalArgumentException("appName parameter is required");
        }
        
        CommandRequest request = new CommandRequest();
        request.setComputerName(computer.getName());
        request.setAction("install_app");
        request.setTargetIp(computer.getIpAddress());
        request.setParameters(appName);
        
        Command command = commandService.createCommand(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
    }

    /**
     * Limpiar computadora (Deep Clean / Format).
     * Endpoint: POST /api/computers/{id}/clean
     *
     * @param id Computer ID
     * @return Command created
     */
    @PostMapping("/{id}/clean")
    @Operation(summary = "Limpiar computadora", description = "Realiza limpieza profunda (formateo lógico, eliminar usuarios intrusos)")
    public ResponseEntity<Command> cleanComputer(@PathVariable Long id) {
        Computer computer = computerService.getComputerById(id);
        
        CommandRequest request = new CommandRequest();
        request.setComputerName(computer.getName());
        request.setAction("format");
        request.setTargetIp(computer.getIpAddress());
        request.setParameters("");
        
        Command command = commandService.createCommand(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
    }

    /**
     * Verificar conexión a internet.
     * Endpoint: GET /api/computers/{id}/check-connection
     *
     * @param id Computer ID
     * @return Command created
     */
    @GetMapping("/{id}/check-connection")
    @Operation(summary = "Verificar conexión", description = "Verifica si la computadora tiene conexión a internet")
    public ResponseEntity<Command> checkConnection(@PathVariable Long id) {
        Computer computer = computerService.getComputerById(id);
        
        CommandRequest request = new CommandRequest();
        request.setComputerName(computer.getName());
        request.setAction("check_connection");
        request.setTargetIp(computer.getIpAddress());
        request.setParameters("");
        
        Command command = commandService.createCommand(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(command);
    }
}

