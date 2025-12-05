package com.envyguard.backend.controller;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/control")
@CrossOrigin(origins = "*") // Permite que React llame a este endpoint
public class SpyController {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @PostMapping("/{action}") // action puede ser START o STOP
    public String controlExam(@PathVariable String action) {
        // Enviar mensaje a todos los agentes (Fanout exchange)
        // exchange: "spy.control", routingKey: "" (fanout lo ignora)
        amqpTemplate.convertAndSend("spy.control", "", action.toUpperCase());
        return "Orden enviada: " + action;
    }
}