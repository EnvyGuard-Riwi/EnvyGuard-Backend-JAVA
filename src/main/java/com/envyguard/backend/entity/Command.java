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

    @Column(name = "computer_name", nullable = false)
    @Schema(description = "Nombre del equipo donde se ejecuta el comando", example = "PC-LAB-01")
    private String computerName;

    @Column(name = "target_ip")
    @Schema(description = "IP del equipo objetivo", example = "192.168.1.100")
    private String targetIp;

    @Column(name = "mac_address")
    @Schema(description = "Dirección MAC del equipo", example = "00:1A:2B:3C:4D:5E")
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