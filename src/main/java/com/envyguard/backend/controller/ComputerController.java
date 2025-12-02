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
 * Controlador REST para gestión de computadores.
 */
@RestController
@RequestMapping("/computers")
@RequiredArgsConstructor
public class ComputerController {

    private final ComputerService computerService;

    /**
     * Crea un nuevo computador.
     * Endpoint: POST /api/computers
     *
     * @param request Mapa con name, ipAddress, macAddress, labName
     * @return Computer creado
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
     * Obtiene todos los computadores.
     * Endpoint: GET /api/computers
     *
     * @return Lista de todos los computadores
     */
    @GetMapping
    public ResponseEntity<List<Computer>> getAllComputers() {
        List<Computer> computers = computerService.getAllComputers();
        return ResponseEntity.ok(computers);
    }

    /**
     * Obtiene un computador por su nombre.
     * Endpoint: GET /api/computers/name/{name}
     *
     * @param name Nombre del computador
     * @return Computer encontrado
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Computer> getComputerByName(@PathVariable String name) {
        Computer computer = computerService.getComputerByName(name);
        return ResponseEntity.ok(computer);
    }

    /**
     * Obtiene un computador por su ID.
     * Endpoint: GET /api/computers/{id}
     *
     * @param id ID del computador
     * @return Computer encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Computer> getComputerById(@PathVariable Long id) {
        Computer computer = computerService.getComputerById(id);
        return ResponseEntity.ok(computer);
    }

    /**
     * Actualiza el estado de un computador.
     * Endpoint: PUT /api/computers/{name}/status
     *
     * @param name Nombre del computador
     * @param request Mapa con status
     * @return Computer actualizado
     */
    @PutMapping("/{name}/status")
    public ResponseEntity<Computer> updateComputerStatus(
            @PathVariable String name,
            @RequestBody Map<String, String> request) {
        Computer.ComputerStatus status = Computer.ComputerStatus.valueOf(request.get("status"));
        Computer computer = computerService.updateComputerStatus(name, status);
        return ResponseEntity.ok(computer);
    }

    /**
     * Actualiza la información de un computador.
     * Endpoint: PUT /api/computers/{id}
     *
     * @param id ID del computador
     * @param request Mapa con ipAddress, macAddress, labName
     * @return Computer actualizado
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
     * Elimina un computador.
     * Endpoint: DELETE /api/computers/{id}
     *
     * @param id ID del computador
     * @return Mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteComputer(@PathVariable Long id) {
        computerService.deleteComputer(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Computer deleted successfully");
        return ResponseEntity.ok(response);
    }
}

