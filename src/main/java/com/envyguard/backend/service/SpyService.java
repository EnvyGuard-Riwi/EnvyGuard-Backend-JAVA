package com.envyguard.backend.service;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SpyService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Cola durable que persiste después de reiniciar, binding a amq.topic con key
    // spy.screens
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "java_spy_bridge", durable = "true", autoDelete = "false"), exchange = @Exchange(value = "amq.topic", type = "topic", ignoreDeclarationExceptions = "true", durable = "true"), key = "spy.screens"))
    public void receiveAndBroadcast(org.springframework.amqp.core.Message message) {
        try {
            // Validar que el mensaje no sea nulo
            if (message == null || message.getBody() == null) {
                System.out.println("⚠️ MENSAJE NULO RECIBIDO - IGNORADO");
                return;
            }

            String jsonMessage = new String(message.getBody());

            // Validar que el mensaje no esté vacío
            if (jsonMessage == null || jsonMessage.trim().isEmpty()) {
                System.out.println("⚠️ MENSAJE VACÍO RECIBIDO - IGNORADO");
                return;
            }

            // IMPRIMIR EN CONSOLA PARA VERIFICAR
            System.out.println("🟢 JAVA RECIBIÓ MENSAJE (Longitud: " + jsonMessage.length() + ")");

            // Reenviar a React
            messagingTemplate.convertAndSend("/topic/screens", jsonMessage);

        } catch (Exception e) {
            // Capturar cualquier error y evitar que se propague
            System.err.println("❌ ERROR AL PROCESAR MENSAJE: " + e.getMessage());
            // NO relanzar la excepción para evitar bucles infinitos
        }
    }
}