package com.envyguard.backend.service;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = false)
public class SpyService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // CAMBIO CLAVE: key = "#" significa "Escuchar TODO lo que llegue al topic"
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "java_spy_bridge", autoDelete = "true"), exchange = @Exchange(value = "amq.topic", type = "topic", ignoreDeclarationExceptions = "true"), key = "#"))
    public void receiveAndBroadcast(String jsonMessage) {
        // IMPRIMIR EN CONSOLA PARA VERIFICAR
        System.out.println("🟢 JAVA RECIBIÓ MENSAJE (Longitud: " + jsonMessage.length() + ")");

        // Reenviar a React
        messagingTemplate.convertAndSend("/topic/screens", jsonMessage);
    }
}