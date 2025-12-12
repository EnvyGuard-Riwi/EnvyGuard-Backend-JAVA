package com.envyguard.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitudes de comandos remotos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para crear un comando remoto")
public class CommandRequest {

    @NotBlank(message = "Computer name is required")
    @Schema(description = "Nombre del equipo donde se ejecutará el comando", 
            example = "PC-LAB-01")
    private String computerName;

    @NotBlank(message = "Action is required")
    @Schema(description = "Acción a ejecutar en el equipo remoto. Valores: SHUTDOWN, REBOOT, WAKE_ON_LAN, LOCK_SESSION, BLOCK_WEBSITE", 
            example = "SHUTDOWN")
    private String action;

    @Schema(description = "Dirección IP del equipo objetivo (requerido para WAKE_ON_LAN y algunos comandos)", 
            example = "192.168.1.100")
    private String targetIp;

    @Schema(description = "Dirección MAC del equipo (requerido para WAKE_ON_LAN)", 
            example = "00:1A:2B:3C:4D:5E")
    private String macAddress;

    @Schema(description = "Parámetros adicionales para el comando en formato JSON o texto", 
            example = "{\"delay\": 30, \"force\": true}")
    private String parameters;
}
