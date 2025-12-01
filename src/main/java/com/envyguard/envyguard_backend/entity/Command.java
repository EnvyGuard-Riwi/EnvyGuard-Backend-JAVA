package com.envyguard.envyguard_backend.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(name = "commands")
@Data
public class Command {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String computerName; // Referencia al computer por nombre
    
    @Column(nullable = false)
    private String commandType; // SHUTDOWN, REBOOT, BLOCK_SITE, UNBLOCK_SITE, LOCK_SCREEN
    
    @JdbcTypeCode(SqlTypes.JSON)
    private String parameters; // JSON como string: {"site": "facebook.com"}
    
    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, SENT, EXECUTED, FAILED
    
    private LocalDateTime sentAt;
    private LocalDateTime executedAt;
    
    private String resultMessage;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    // Constructor por defecto
    public Command() {}
    
    // Constructor simplificado
    public Command(String computerName, String commandType) {
        this.computerName = computerName;
        this.commandType = commandType;
        this.status = "PENDING";
    }
    
    // Método para establecer parámetros como JSON
    public void setParametersAsJson(Object parameters) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.parameters = mapper.writeValueAsString(parameters);
        } catch (Exception e) {
            this.parameters = "{}";
        }
    }
    
    // Método para obtener parámetros como objeto
    public <T> T getParametersAsObject(Class<T> type) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(this.parameters, type);
        } catch (Exception e) {
            return null;
        }
    }
}