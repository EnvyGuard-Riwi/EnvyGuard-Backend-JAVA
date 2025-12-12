package com.envyguard.backend.service;

import com.envyguard.backend.config.RabbitMQConfig;
import com.envyguard.backend.dto.CommandMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Service for sending messages to RabbitMQ.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Sends a command message to RabbitMQ queue.
     *
     * @param commandMessage Command message to send
     */
    public void sendCommand(CommandMessage commandMessage) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.PC_COMMANDS_QUEUE, commandMessage);
            log.info("Command {} sent to RabbitMQ for computer {}",
                    commandMessage.getCommandId(), commandMessage.getComputerName());
        } catch (Exception e) {
            log.error("Error sending command to RabbitMQ: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send command to RabbitMQ", e);
        }
    }
}
