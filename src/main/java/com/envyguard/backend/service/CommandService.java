package com.envyguard.backend.service;

import com.envyguard.backend.dto.CommandMessage;
import com.envyguard.backend.dto.CommandRequest;
import com.envyguard.backend.entity.*;
import com.envyguard.backend.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing remote commands.
 * Handles command creation, database persistence, and RabbitMQ message sending.
 */
@Service
@Slf4j
public class CommandService {

    private final CommandRepository commandRepository;
    private final Sala1Repository sala1Repository;
    private final Sala2Repository sala2Repository;
    private final Sala3Repository sala3Repository;
    private final Sala4Repository sala4Repository;

    @Autowired(required = false)
    private RabbitMQService rabbitMQService;

    public CommandService(CommandRepository commandRepository,
                          Sala1Repository sala1Repository,
                          Sala2Repository sala2Repository,
                          Sala3Repository sala3Repository,
                          Sala4Repository sala4Repository) {
        this.commandRepository = commandRepository;
        this.sala1Repository = sala1Repository;
        this.sala2Repository = sala2Repository;
        this.sala3Repository = sala3Repository;
        this.sala4Repository = sala4Repository;
    }

    /**
     * Creates a new command for a computer.
     * Validates that the computer exists in the specified sala before creating the command.
     *
     * @param request CommandRequest with command data (salaNumber and pcId)
     * @return Created command
     * @throws IllegalArgumentException If computer does not exist in the specified sala
     */
    @Transactional
    public Command createCommand(CommandRequest request) {
        // Validar que salaNumber esté entre 1 y 4
        Integer salaNumber = request.getSalaNumber();
        if (salaNumber < 1 || salaNumber > 4) {
            throw new IllegalArgumentException("Número de sala inválido: " + salaNumber + ". Debe estar entre 1 y 4.");
        }

        // Buscar el PC en la sala correspondiente
        String computerName = null;
        String targetIp = null;
        String macAddress = null;

        switch (salaNumber) {
            case 1:
                Sala1 pc1 = sala1Repository.findById(request.getPcId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "PC no encontrado en Sala 1 con ID: " + request.getPcId()));
                computerName = pc1.getNombrePc();
                targetIp = pc1.getIp();
                macAddress = pc1.getMac();
                break;
            case 2:
                Sala2 pc2 = sala2Repository.findById(request.getPcId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "PC no encontrado en Sala 2 con ID: " + request.getPcId()));
                computerName = pc2.getNombrePc();
                targetIp = pc2.getIp();
                macAddress = pc2.getMac();
                break;
            case 3:
                Sala3 pc3 = sala3Repository.findById(request.getPcId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "PC no encontrado en Sala 3 con ID: " + request.getPcId()));
                computerName = pc3.getNombrePc();
                targetIp = pc3.getIp();
                macAddress = pc3.getMac();
                break;
            case 4:
                Sala4 pc4 = sala4Repository.findById(request.getPcId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "PC no encontrado en Sala 4 con ID: " + request.getPcId()));
                computerName = pc4.getNombrePc();
                targetIp = pc4.getIp();
                macAddress = pc4.getMac();
                break;
        }

        // Obtener el email del usuario autenticado
        String userEmail = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            userEmail = authentication.getName();
        }

        // Crear el comando con toda la información
        Command command = Command.builder()
                .salaNumber(salaNumber)
                .pcId(request.getPcId())
                .computerName(computerName)
                .targetIp(targetIp)
                .macAddress(macAddress)
                .action(request.getAction())
                .parameters(request.getParameters())
                .status(Command.CommandStatus.PENDING)
                .userEmail(userEmail)
                .build();

        command = commandRepository.save(command);

        // Intentar enviar a RabbitMQ si el servicio está disponible
        if (rabbitMQService != null) {
            try {
                CommandMessage message = CommandMessage.builder()
                        .commandId(command.getId())
                        .computerName(command.getComputerName())
                        .action(command.getAction())
                        .targetIp(command.getTargetIp())
                        .macAddress(command.getMacAddress())
                        .parameters(command.getParameters())
                        .timestamp(LocalDateTime.now())
                        .build();

                rabbitMQService.sendCommand(message);
                command.setStatus(Command.CommandStatus.SENT);
                command.setSentAt(LocalDateTime.now());
                command = commandRepository.save(command);
                log.info("Comando {} enviado a RabbitMQ exitosamente para {} en Sala {}", 
                         command.getId(), computerName, salaNumber);
            } catch (Exception e) {
                log.error("Error al enviar comando {} a RabbitMQ: {}", command.getId(), e.getMessage());
                command.setStatus(Command.CommandStatus.FAILED);
                command.setResultMessage("Error al enviar a RabbitMQ: " + e.getMessage());
                command = commandRepository.save(command);
            }
        } else {
            log.warn("Servicio RabbitMQ no disponible. Comando {} guardado pero no enviado.", command.getId());
        }

        return command;
    }

    /**
     * Gets all commands.
     *
     * @return List of all commands
     */
    public List<Command> getAllCommands() {
        return commandRepository.findAll();
    }

    /**
     * Gets all commands for a specific computer.
     *
     * @param computerName Computer name
     * @return List of commands
     */
    public List<Command> getCommandsByComputer(String computerName) {
        return commandRepository.findByComputerName(computerName);
    }

    /**
     * Gets all commands with a specific status.
     *
     * @param status Command status
     * @return List of commands
     */
    public List<Command> getCommandsByStatus(Command.CommandStatus status) {
        return commandRepository.findByStatus(status);
    }

    /**
     * Gets a command by its ID.
     *
     * @param id Command ID
     * @return Found command
     * @throws IllegalArgumentException If command does not exist
     */
    public Command getCommandById(Long id) {
        return commandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Command not found: " + id));
    }

    /**
     * Updates a command's status.
     * Used when the C# agent reports the execution result.
     *
     * @param commandId     Command ID
     * @param status        New status
     * @param resultMessage Result message (optional)
     * @return Updated command
     */
    @Transactional
    public Command updateCommandStatus(Long commandId, Command.CommandStatus status, String resultMessage) {
        Command command = getCommandById(commandId);
        command.setStatus(status);
        command.setResultMessage(resultMessage);

        if (status == Command.CommandStatus.EXECUTED || status == Command.CommandStatus.FAILED) {
            command.setExecutedAt(LocalDateTime.now());
        }

        return commandRepository.save(command);
    }
}
