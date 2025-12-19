package com.envyguard.backend.service;

import com.envyguard.backend.dto.ComputerStatusDto;
import com.envyguard.backend.entity.Computer;
import com.envyguard.backend.repository.ComputerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComputerStatusService {

    private final ComputerRepository computerRepository;
    private final com.envyguard.backend.repository.Sala4Repository sala4Repository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "pc_status_updates")
    public void updateStatus(String jsonMessage) {
        try {
            log.debug("Received status update: {}", jsonMessage);
            ComputerStatusDto statusDto = objectMapper.readValue(jsonMessage, ComputerStatusDto.class);

            // 1. Update general Computers table (for Dashboard status)
            Computer computer = computerRepository.findByIpAddress(statusDto.getIpAddress())
                    .orElseGet(() -> {
                        // Optional: Create new if not exists, or just log warning.
                        // For auto-discovery, we create it.
                        Computer newComputer = new Computer();
                        newComputer.setIpAddress(statusDto.getIpAddress());
                        newComputer.setName(statusDto.getHostname() != null ? statusDto.getHostname()
                                : "Unknown-" + statusDto.getIpAddress());
                        return newComputer;
                    });

            try {
                computer.setStatus(Computer.ComputerStatus.valueOf(statusDto.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                computer.setStatus(Computer.ComputerStatus.UNKNOWN);
            }

            computer.setLastSeen(LocalDateTime.now());
            if (statusDto.getHostname() != null && !statusDto.getHostname().isEmpty()) {
                computer.setName(statusDto.getHostname());
            }

            Computer saved = computerRepository.save(computer);

            // 2. Update Sala4 table (Specific request for legacy support)
            try {
                java.util.Optional<com.envyguard.backend.entity.Sala4> sala4Opt = sala4Repository
                        .findByIp(statusDto.getIpAddress());
                if (sala4Opt.isPresent()) {
                    com.envyguard.backend.entity.Sala4 sala4 = sala4Opt.get();
                    try {
                        sala4.setStatus(com.envyguard.backend.entity.Computer.ComputerStatus
                                .valueOf(statusDto.getStatus().toUpperCase()));
                    } catch (Exception e) {
                        sala4.setStatus(com.envyguard.backend.entity.Computer.ComputerStatus.UNKNOWN);
                    }
                    sala4.setLastSeen(LocalDateTime.now());
                    sala4Repository.save(sala4);
                    log.debug("Updated Sala4 status for IP: {}", statusDto.getIpAddress());
                }
            } catch (Exception e) {
                log.warn("Could not update Sala4 status: {}", e.getMessage());
            }

            // Broadcast to frontend
            messagingTemplate.convertAndSend("/topic/computers", saved);

        } catch (Exception e) {
            log.error("Error processing status update: {}", e.getMessage(), e);
        }
    }
}
