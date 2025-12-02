package com.envyguard.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Representa un computador en el sistema EnvyGuard.
 * Almacena información sobre el estado, identificación y configuración de cada equipo.
 */
@Entity
@Table(name = "computers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Computer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "mac_address")
    private String macAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ComputerStatus status = ComputerStatus.OFFLINE;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "lab_name")
    private String labName;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum ComputerStatus {
        ONLINE,
        OFFLINE,
        UNKNOWN
    }
}
