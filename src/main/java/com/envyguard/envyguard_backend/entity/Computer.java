package com.envyguard.envyguard_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "computers")
@Data
public class Computer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name; // ej: "lab1-pc05", "pc-01"
    
    @Column(unique = true)
    private String macAddress;
    
    private String ipAddress;
    
    @Column(nullable = false)
    private String status = "OFFLINE"; // ONLINE, OFFLINE
    
    private LocalDateTime lastSeen;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    // Constructor por defecto
    public Computer() {}
    
    // Constructor para creación
    public Computer(String name, String macAddress) {
        this.name = name;
        this.macAddress = macAddress;
        this.status = "OFFLINE";
    }
}
