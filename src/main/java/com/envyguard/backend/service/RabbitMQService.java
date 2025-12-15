package com.envyguard.backend.service;

import com.envyguard.backend.config.RabbitMQConfig;
import com.envyguard.backend.dto.AgentCommandMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Service for sending messages to RabbitMQ.
 * Sends messages in the format expected by C# agents.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Sends a command message to RabbitMQ queue in agent-compatible format.
     * 
     * Expected format:
     * {
     *   "action": "shutdown",
     *   "targetIp": "192.168.1.50",
     *   "parameters": ""
     * }
     *
     * @param agentMessage Command message in agent format
     */
    public void sendCommand(AgentCommandMessage agentMessage) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.PC_COMMANDS_QUEUE, agentMessage);
            log.info("Command sent to RabbitMQ: action={}, targetIp={}",
                    agentMessage.getAction(), agentMessage.getTargetIp());
        } catch (Exception e) {
            log.error("Error sending command to RabbitMQ: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send command to RabbitMQ", e);
        }
    }
}
