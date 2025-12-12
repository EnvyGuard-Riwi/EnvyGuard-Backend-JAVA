package com.envyguard.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * REST controller for computer management.
 * 
 * ⚠️ DEPRECADO: Este controlador está siendo reemplazado por la nueva arquitectura basada en Salas.
 * 
 * NUEVA ESTRUCTURA:
 * - Las computadoras ya no están en una tabla genérica 'computers'
 * - Ahora están organizadas por salas: sala_1, sala_2, sala_3, sala_4
 * - Cada PC tiene un ID único dentro de su sala
 * 
 * ENDPOINTS ACTUALIZADOS:
 * - Para enviar comandos, usa: POST /api/commands
 *   Ejemplo: {"salaNumber": 4, "pcId": 1, "action": "SHUTDOWN", "parameters": ""}
 * 
 * - Para ver comandos: GET /api/commands
 * - Para ver comandos por estado: GET /api/commands/status/{status}
 * - Para ver un comando específico: GET /api/commands/{id}
 * 
 * TODO: 
 * - Crear SalaController para gestionar las salas y listar PCs por sala
 * - Eliminar ComputerController y ComputerService cuando se migre el frontend
 */
@RestController
@RequestMapping("/computers")
@RequiredArgsConstructor
public class ComputerController {

    /**
     * Endpoint de información sobre la migración a la nueva arquitectura.
     */
    @GetMapping("/migration-info")
    public ResponseEntity<Map<String, String>> getMigrationInfo() {
        return ResponseEntity.ok(Map.of(
            "status", "DEPRECATED",
            "message", "Este endpoint está deprecado. Por favor usa /api/commands para enviar comandos.",
            "newEndpoint", "POST /api/commands",
            "example", "{\"salaNumber\": 4, \"pcId\": 1, \"action\": \"SHUTDOWN\"}"
        ));
    }
}
