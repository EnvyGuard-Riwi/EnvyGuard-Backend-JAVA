package com.envyguard.backend;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.Test;

public class RabbitMQManualTest {

    @Test
    public void testConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq.envy.crudzaso.com");
        factory.setPort(5672);
        factory.setUsername("v9q14Pg15x");
        factory.setPassword("Ki4Z77IRipVLG9Y1LahdxN2twlLUZ0");
        factory.setVirtualHost("/");

        try (Connection connection = factory.newConnection()) {
            System.out.println("SUCCESS: Connected to RabbitMQ successfully!");
        } catch (Exception e) {
            System.err.println("FAILURE: Could not connect to RabbitMQ.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
