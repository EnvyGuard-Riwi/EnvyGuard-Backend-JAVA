package com.envyguard.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "commands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa un comando remoto enviado a un equipo")
public class Command {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del comando", example = "1")
    private Long id;

    @Column(name = "sala_number", nullable = false)
    @Schema(description = "Número de la sala donde está el PC (1-4)", example = "4")
    private Integer salaNumber;

    @Column(name = "pc_id", nullable = false)
    @Schema(description = "ID del PC en la tabla sala_X", example = "1")
    private Long pcId;

    @Column(name = "computer_name", nullable = false)
    @Schema(description = "Nombre del PC (ej: 'PC 1', obtenido automáticamente de sala_X)", example = "PC 1")
    private String computerName;

    @Column(name = "target_ip")
    @Schema(description = "IP del PC (obtenida automáticamente de sala_X)", example = "10.0.120.2")
    private String targetIp;

    @Column(name = "mac_address")
    @Schema(description = "MAC del PC (obtenida automáticamente de sala_X)", example = "08:bf:b8:03:13:0f")
    private String macAddress;

    @Column(name = "action", nullable = false)
    @Schema(description = "Acción a ejecutar", example = "SHUTDOWN")
    private String action;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Parámetros adicionales del comando en formato JSON")
    private String parameters;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "Estado actual del comando", example = "EXECUTED")
    private CommandStatus status = CommandStatus.PENDING;

    @Column(name = "sent_at")
    @Schema(description = "Fecha y hora en que se envió el comando")
    private LocalDateTime sentAt;

    @Column(name = "executed_at")
    @Schema(description = "Fecha y hora en que se ejecutó el comando")
    private LocalDateTime executedAt;

    @Column(name = "result_message", length = 500)
    @Schema(description = "Mensaje con el resultado de la ejecución")
    private String resultMessage;

    @Column(name = "created_at", updatable = false)
    @Schema(description = "Fecha y hora de creación del comando")
    private LocalDateTime createdAt;

    @Column(name = "user_email")
    @Schema(description = "Email del usuario que ejecutó el comando (para auditoría)", example = "admin@envyguard.com")
    private String userEmail;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum CommandType {
        SHUTDOWN,
        REBOOT,
        BLOCK_WEBSITE,
        LOCK_SESSION
    }

    public enum CommandStatus {
        PENDING,
        SENT,
        EXECUTED,
        FAILED
    }
}