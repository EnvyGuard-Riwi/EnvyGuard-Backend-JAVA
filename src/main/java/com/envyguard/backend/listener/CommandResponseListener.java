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
    public void handleCommandResponse(CommandResponse response) {
        try {
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
            log.error("Error processing command response for command {}: {}", 
                    response.getCommandId(), e.getMessage(), e);
        }
    }
}

