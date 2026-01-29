package com.envyguard.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for exam monitoring control.
 * Sends START/STOP commands to monitoring agents.
 */
@RestController
@RequestMapping("/control")
@CrossOrigin(origins = "*")
@Tag(name = "Exam Control", description = "Exam monitoring control API")
public class SpyController {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Operation(summary = "Control exam monitoring", description = "Sends START or STOP command to all monitoring agents via RabbitMQ fanout exchange.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Control command sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid action (use START or STOP)")
    })
    @PostMapping("/{action}")
    public String controlExam(
            @Parameter(description = "Control action (START or STOP)", example = "START") @PathVariable String action) {
        // Send message to all agents (Fanout exchange)
        // exchange: "spy.control", routingKey: "" (fanout ignores it)
        amqpTemplate.convertAndSend("spy.control", "", action.toUpperCase());
        return "Order sent: " + action;
    }
}