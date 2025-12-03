package com.envyguard.backend.service;

import com.envyguard.backend.dto.CommandMessage;
import com.envyguard.backend.dto.CommandRequest;
import com.envyguard.backend.entity.Command;
import com.envyguard.backend.repository.CommandRepository;
import com.envyguard.backend.repository.ComputerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing remote commands.
 * Handles command creation, database persistence, and RabbitMQ message sending.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommandService {

    private final CommandRepository commandRepository;
    private final ComputerRepository computerRepository;
    private final RabbitMQService rabbitMQService;

    /**
     * Creates a new command for a computer.
     * Validates that the computer exists before creating the command.
     *
     * @param request CommandRequest with command data
     * @return Created command
     * @throws IllegalArgumentException If computer does not exist
     */
    @Transactional
    public Command createCommand(CommandRequest request) {
        if (!computerRepository.existsByName(request.getComputerName())) {
            throw new IllegalArgumentException("Computer not found: " + request.getComputerName());
        }

        Command command = Command.builder()
                .computerName(request.getComputerName())
                .commandType(request.getCommandType())
                .parameters(request.getParameters())
                .status(Command.CommandStatus.PENDING)
                .build();

        command = commandRepository.save(command);

        try {
            CommandMessage message = CommandMessage.builder()
                    .commandId(command.getId())
                    .computerName(command.getComputerName())
                    .commandType(command.getCommandType())
                    .parameters(command.getParameters())
                    .timestamp(LocalDateTime.now())
                    .build();

            rabbitMQService.sendCommand(message);
            command.setStatus(Command.CommandStatus.SENT);
            command.setSentAt(LocalDateTime.now());
            command = commandRepository.save(command);
            log.info("Command {} sent to RabbitMQ successfully", command.getId());
        } catch (Exception e) {
            log.error("Failed to send command {} to RabbitMQ: {}", command.getId(), e.getMessage());
            command.setStatus(Command.CommandStatus.FAILED);
            command.setResultMessage("Failed to send to RabbitMQ: " + e.getMessage());
            command = commandRepository.save(command);
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
     * @param commandId Command ID
     * @param status New status
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
