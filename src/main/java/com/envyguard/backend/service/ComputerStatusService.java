package com.envyguard.backend.service;

import com.envyguard.backend.dto.ComputerStatusDto;

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

    // private final ComputerRepository computerRepository; // Removed field
    private final com.envyguard.backend.repository.Sala4Repository sala4Repository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "pc_status_updates")
    public void updateStatus(org.springframework.amqp.core.Message message) {
        try {
            String jsonMessage = new String(message.getBody());
            log.debug("Received status update: {}", jsonMessage);
            ComputerStatusDto statusDto = objectMapper.readValue(jsonMessage, ComputerStatusDto.class);

            // Update Sala4 table (Now the primary source)
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

                    com.envyguard.backend.entity.Sala4 savedSala4 = sala4Repository.save(sala4);
                    log.debug("Updated Sala4 status for IP: {}", statusDto.getIpAddress());

                    // Broadcast to frontend (Sending Sala4 entity now)
                    messagingTemplate.convertAndSend("/topic/computers", savedSala4);
                } else {
                    log.warn("Computer with IP {} not found in Sala 4", statusDto.getIpAddress());
                }
            } catch (Exception e) {
                log.warn("Could not update Sala4 status: {}", e.getMessage());
            }

        } catch (Exception e) {
            log.error("Error processing status update: {}", e.getMessage(), e);
        }
    }
}
