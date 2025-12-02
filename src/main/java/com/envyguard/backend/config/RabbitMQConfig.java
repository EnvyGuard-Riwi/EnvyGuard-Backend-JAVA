package com.envyguard.backend.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ.
 * Define las colas y la serialización de mensajes.
 * Por ahora está configurado pero no se usará hasta conectar RabbitMQ.
 */
@Configuration
public class RabbitMQConfig {

    public static final String PC_COMMANDS_QUEUE = "pc_commands";
    public static final String PC_RESPONSES_QUEUE = "pc_responses";

    /**
     * Define la cola para comandos enviados a los agentes C#.
     *
     * @return Queue configurada
     */
    @Bean
    public Queue pcCommandsQueue() {
        return new Queue(PC_COMMANDS_QUEUE, true);
    }

    /**
     * Define la cola para respuestas de los agentes C#.
     *
     * @return Queue configurada
     */
    @Bean
    public Queue pcResponsesQueue() {
        return new Queue(PC_RESPONSES_QUEUE, true);
    }

    /**
     * Configura el convertidor de mensajes a JSON.
     *
     * @return MessageConverter configurado
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configura el RabbitTemplate con el convertidor JSON.
     *
     * @param connectionFactory ConnectionFactory de RabbitMQ
     * @return RabbitTemplate configurado
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    /**
     * Configura el listener container factory para recibir mensajes.
     *
     * @param connectionFactory ConnectionFactory de RabbitMQ
     * @return SimpleRabbitListenerContainerFactory configurado
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
}
