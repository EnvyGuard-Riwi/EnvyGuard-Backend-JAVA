package com.envyguard.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a computer in the EnvyGuard system.
 * Stores information about status, identification, and configuration of each
 * computer.
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

    @Column(name = "has_internet")
    @Builder.Default
    private boolean hasInternet = true;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "lab_name")
    private String labName;

    @Column(name = "room_number")
    private Integer roomNumber;

    @Column(name = "position_in_room")
    private String positionInRoom;

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
