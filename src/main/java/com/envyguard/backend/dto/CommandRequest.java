package com.envyguard.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitudes de comandos remotos.
 * Ahora basado en el nuevo esquema: sala_number + pc_id
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para crear un comando remoto en un PC específico de una sala")
public class CommandRequest {

    @NotNull(message = "Sala number is required")
    @Min(value = 1, message = "Sala number must be between 1 and 4")
    @Max(value = 4, message = "Sala number must be between 1 and 4")
    @Schema(description = "Número de la sala donde está el PC (1-4)", 
            example = "4",
            minimum = "1",
            maximum = "4")
    private Integer salaNumber;

    @NotNull(message = "PC ID is required")
    @Min(value = 1, message = "PC ID must be greater than 0")
    @Schema(description = "ID del PC en la tabla sala_X. El backend buscará automáticamente el nombre, IP y MAC del PC.", 
            example = "1")
    private Long pcId;

    @NotBlank(message = "Action is required")
    @Schema(description = """
            Acción a ejecutar en el equipo remoto.
            
            Acciones disponibles:
            - SHUTDOWN: Apaga el equipo inmediatamente
            - REBOOT: Reinicia el equipo
            - WAKE_ON_LAN: Enciende el equipo (solo si está apagado, requiere MAC)
            - LOCK_SESSION: Bloquea la sesión del usuario
            - BLOCK_WEBSITE: Bloquea acceso a un sitio web (requiere dominio en parameters)
            - UNBLOCK_WEBSITE: Desbloquea acceso a un sitio web (requiere dominio en parameters)
            - FORMAT: Formateo lógico / limpieza del sistema
            - TEST: Prueba de conexión con el agente
            - INSTALL_APP: Instala una aplicación normal (requiere nombre de paquete en parameters)
            - INSTALL_SNAP: Instala una aplicación snap (requiere nombre de snap en parameters)
            - DISABLE_INTERNET: Deshabilita la conexión a internet
            - ENABLE_INTERNET: Habilita la conexión a internet
            """, 
            example = "SHUTDOWN",
            allowableValues = {"SHUTDOWN", "REBOOT", "WAKE_ON_LAN", "LOCK_SESSION", "BLOCK_WEBSITE", 
                              "UNBLOCK_WEBSITE", "FORMAT", "TEST", "INSTALL_APP", "INSTALL_SNAP",
                              "DISABLE_INTERNET", "ENABLE_INTERNET"})
    private String action;

    @Schema(description = """
            Parámetros adicionales para el comando en formato JSON.
            
            Ejemplos:
            - Para SHUTDOWN con retraso: {"delay": 30, "force": true}
            - Para BLOCK_WEBSITE: {"url": "facebook.com", "permanent": false}
            """, 
            example = "{\"delay\": 30, \"force\": true}")
    private String parameters;
}
