package com.envyguard.backend.entity;

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
public class Command {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "computer_name", nullable = false)
    private String computerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "command_type", nullable = false)
    private CommandType commandType;

    @Column(columnDefinition = "TEXT")
    private String parameters;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CommandStatus status = CommandStatus.PENDING;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @Column(name = "result_message", length = 500)
    private String resultMessage;

    @Column(name = "created_at", updatable = false)
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