package com.envyguard.backend.service;

import com.envyguard.backend.dto.CommandRequest;
import com.envyguard.backend.entity.Command;
import com.envyguard.backend.entity.Computer;
import com.envyguard.backend.repository.CommandRepository;
import com.envyguard.backend.repository.ComputerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestionar comandos remotos.
 * Por ahora solo guarda comandos en la base de datos.
 * La integración con RabbitMQ se agregará posteriormente.
 */
@Service
@RequiredArgsConstructor
public class CommandService {

    private final CommandRepository commandRepository;
    private final ComputerRepository computerRepository;

    /**
     * Crea un nuevo comando para un computador.
     * Valida que el computador exista antes de crear el comando.
     *
     * @param request CommandRequest con los datos del comando
     * @return Command creado
     * @throws IllegalArgumentException Si el computador no existe
     */
    @Transactional
    public Command createCommand(CommandRequest request) {
        Computer computer = computerRepository.findByName(request.getComputerName())
                .orElseThrow(() -> new IllegalArgumentException("Computer not found: " + request.getComputerName()));

        Command command = Command.builder()
                .computerName(request.getComputerName())
                .commandType(request.getCommandType())
                .parameters(request.getParameters())
                .status(Command.CommandStatus.PENDING)
                .sentAt(LocalDateTime.now())
                .build();

        return commandRepository.save(command);
    }

    /**
     * Obtiene todos los comandos de un computador específico.
     *
     * @param computerName Nombre del computador
     * @return Lista de comandos
     */
    public List<Command> getCommandsByComputer(String computerName) {
        return commandRepository.findByComputerName(computerName);
    }

    /**
     * Obtiene todos los comandos con un estado específico.
     *
     * @param status Estado del comando
     * @return Lista de comandos
     */
    public List<Command> getCommandsByStatus(Command.CommandStatus status) {
        return commandRepository.findByStatus(status);
    }

    /**
     * Obtiene un comando por su ID.
     *
     * @param id ID del comando
     * @return Command encontrado
     * @throws IllegalArgumentException Si el comando no existe
     */
    public Command getCommandById(Long id) {
        return commandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Command not found: " + id));
    }

    /**
     * Actualiza el estado de un comando.
     * Usado cuando el agente C# reporta el resultado de la ejecución.
     *
     * @param commandId ID del comando
     * @param status Nuevo estado
     * @param resultMessage Mensaje de resultado (opcional)
     * @return Command actualizado
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
