package com.envyguard.backend;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Manual integration test for RabbitMQ connection.
 * This test is disabled by default as it requires external RabbitMQ server.
 * Run manually when needed to verify RabbitMQ connectivity.
 */
@Disabled("Manual integration test - run only when needed")
public class RabbitMQManualTest {

    @Test
    public void testConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq.envy.crudzaso.com");
        factory.setPort(5672);
        factory.setUsername(System.getenv("RABBITMQ_USERNAME"));
        factory.setPassword(System.getenv("RABBITMQ_PASSWORD"));
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
