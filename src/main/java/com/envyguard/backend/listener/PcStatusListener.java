package com.envyguard.backend.listener;

import com.envyguard.backend.config.RabbitMQConfig;
import com.envyguard.backend.service.ComputerService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Listener for PC status updates from C# agents via RabbitMQ.
 * Only processes messages when status CHANGES (optimization to reduce traffic).
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class PcStatusListener {

    private final ComputerService computerService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Listens for PC status updates from the pc_status_updates queue.
     * Processes changes and broadcasts to WebSocket clients.
     *
     * @param message Raw message from RabbitMQ
     */
    @RabbitListener(queues = RabbitMQConfig.PC_STATUS_UPDATES_QUEUE)
    public void handleStatusUpdate(org.springframework.amqp.core.Message message) {
        try {
            if (message == null || message.getBody() == null) {
                log.warn("Received null status message - ignoring");
                return;
            }

            String jsonMessage = new String(message.getBody());

            if (jsonMessage == null || jsonMessage.trim().isEmpty()) {
                log.warn("Received empty status message - ignoring");
                return;
            }

            // Parse the status update
            PcStatusUpdate statusUpdate;
            try {
                statusUpdate = objectMapper.readValue(jsonMessage, PcStatusUpdate.class);
            } catch (Exception e) {
                log.error("Failed to parse status update: {}", e.getMessage());
                return;
            }

            if (statusUpdate == null || statusUpdate.getPcId() == null) {
                log.warn("Received invalid status update - ignoring");
                return;
            }

            log.info("📊 Status change: PC {} ({}) is now {}",
                    statusUpdate.getPcName(),
                    statusUpdate.getIpAddress(),
                    statusUpdate.getStatus());

            // Update status in database
            try {
                computerService.updateComputerStatus(
                        statusUpdate.getPcId(),
                        statusUpdate.getStatus());
            } catch (Exception e) {
                log.error("Failed to update PC status in database: {}", e.getMessage());
            }

            // Broadcast to WebSocket clients
            try {
                messagingTemplate.convertAndSend("/topic/pc-status", statusUpdate);
                log.debug("Broadcasted status update via WebSocket");
            } catch (Exception e) {
                log.error("Failed to broadcast status via WebSocket: {}", e.getMessage());
            }

        } catch (Exception e) {
            log.error("Error processing status update: {}", e.getMessage(), e);
        }
    }

    /**
     * DTO for PC status updates from C# agent.
     */
    @Data
    public static class PcStatusUpdate {
        @JsonProperty("PcId")
        private Long pcId;

        @JsonProperty("PcName")
        private String pcName;

        @JsonProperty("IpAddress")
        private String ipAddress;

        @JsonProperty("MacAddress")
        private String macAddress;

        @JsonProperty("Status")
        private String status;

        @JsonProperty("Timestamp")
        private String timestamp;
    }
}
