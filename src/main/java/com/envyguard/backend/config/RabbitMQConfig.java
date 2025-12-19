package com.envyguard.backend.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * RabbitMQ configuration.
 * Defines queues and message serialization.
 * Currently configured but not used until RabbitMQ is connected.
 */
@Configuration
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitMQConfig {

    public static final String PC_COMMANDS_QUEUE = "pc_commands";
    public static final String PC_RESPONSES_QUEUE = "pc_responses";
    public static final String PC_STATUS_UPDATES_QUEUE = "pc_status_updates";

    /**
     * Defines the queue for commands sent to C# agents.
     *
     * @return Configured queue
     */
    @Bean
    public Queue pcCommandsQueue() {
        return new Queue(PC_COMMANDS_QUEUE, true);
    }

    /**
     * Defines the queue for responses from C# agents.
     *
     * @return Configured queue
     */
    @Bean
    public Queue pcResponsesQueue() {
        return new Queue(PC_RESPONSES_QUEUE, true);
    }

    /**
     * Defines the queue for PC status updates.
     *
     * @return Configured queue
     */
    @Bean
    public Queue pcStatusUpdatesQueue() {
        return new Queue(PC_STATUS_UPDATES_QUEUE, true);
    }

    /**
     * Configures the JSON message converter.
     *
     * @return Configured message converter
     */
    @Bean
    @java.lang.SuppressWarnings("removal")
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * Configures RabbitTemplate with JSON converter.
     *
     * @param connectionFactory RabbitMQ connection factory
     * @return Configured RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    /**
     * Configures the listener container factory for receiving messages.
     *
     * @param connectionFactory RabbitMQ connection factory
     * @return Configured SimpleRabbitListenerContainerFactory
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // factory.setMessageConverter(jsonMessageConverter()); // Removed to avoid
        // automatic header mapping issues
        factory.setMissingQueuesFatal(false);
        return factory;
    }
}
