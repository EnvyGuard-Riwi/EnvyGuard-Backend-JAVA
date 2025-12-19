package com.envyguard.backend.listener;

import com.envyguard.backend.config.RabbitMQConfig;
import com.envyguard.backend.dto.CommandResponse;
import com.envyguard.backend.service.CommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Listener for command responses from C# agents via RabbitMQ.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class CommandResponseListener {

    private final CommandService commandService;

    /**
     * Listens for command responses from the pc_responses queue.
     *
     * @param response CommandResponse from C# agent
     */
    @RabbitListener(queues = RabbitMQConfig.PC_RESPONSES_QUEUE)
    public void handleCommandResponse(org.springframework.amqp.core.Message message) {
        try {
            // Validar que el mensaje no sea nulo
            if (message == null || message.getBody() == null) {
                log.warn("Received null message - ignoring");
                return;
            }

            String jsonMessage = new String(message.getBody());
            
            // Validar que el mensaje no esté vacío
            if (jsonMessage == null || jsonMessage.trim().isEmpty()) {
                log.warn("Received empty message - ignoring");
                return;
            }

            // Parsear el mensaje manualmente
            CommandResponse response;
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                response = mapper.readValue(jsonMessage, CommandResponse.class);
            } catch (Exception e) {
                log.error("Failed to parse command response: {}", e.getMessage());
                return; // No relanzar la excepción
            }

            // Validar que la respuesta tenga datos válidos
            if (response == null || response.getCommandId() == null) {
                log.warn("Received invalid command response - ignoring");
                return;
            }

            log.info("Received command response for command {}: {}", 
                    response.getCommandId(), response.getStatus());
            
            commandService.updateCommandStatus(
                    response.getCommandId(),
                    response.getStatus(),
                    response.getResultMessage()
            );
            
            log.info("Command {} status updated to {}", 
                    response.getCommandId(), response.getStatus());
        } catch (Exception e) {
            log.error("Error processing command response: {}", e.getMessage(), e);
            // NO relanzar la excepción para evitar bucles infinitos
        }
    }
}

