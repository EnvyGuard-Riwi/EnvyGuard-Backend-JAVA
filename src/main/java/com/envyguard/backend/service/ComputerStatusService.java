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
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "pc_status_updates")
    public void updateStatus(String jsonMessage) {
        try {
            log.debug("Received status update: {}", jsonMessage);
            ComputerStatusDto statusDto = objectMapper.readValue(jsonMessage, ComputerStatusDto.class);

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

            // Broadcast to frontend
            messagingTemplate.convertAndSend("/topic/computers", saved);

        } catch (Exception e) {
            log.error("Error processing status update: {}", e.getMessage(), e);
        }
    }
}
