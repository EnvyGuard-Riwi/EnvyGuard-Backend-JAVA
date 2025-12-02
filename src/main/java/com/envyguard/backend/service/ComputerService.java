package com.envyguard.backend.service;

import com.envyguard.backend.entity.Computer;
import com.envyguard.backend.repository.ComputerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestionar computadores en el sistema.
 */
@Service
@RequiredArgsConstructor
public class ComputerService {

    private final ComputerRepository computerRepository;

    /**
     * Crea un nuevo computador en el sistema.
     *
     * @param name Nombre único del computador
     * @param ipAddress Dirección IP (opcional)
     * @param macAddress Dirección MAC (opcional)
     * @param labName Nombre del laboratorio (opcional)
     * @return Computer creado
     * @throws IllegalArgumentException Si el nombre ya existe
     */
    @Transactional
    public Computer createComputer(String name, String ipAddress, String macAddress, String labName) {
        if (computerRepository.existsByName(name)) {
            throw new IllegalArgumentException("Computer name already exists: " + name);
        }

        Computer computer = Computer.builder()
                .name(name)
                .ipAddress(ipAddress)
                .macAddress(macAddress)
                .labName(labName)
                .status(Computer.ComputerStatus.OFFLINE)
                .build();

        return computerRepository.save(computer);
    }

    /**
     * Obtiene todos los computadores.
     *
     * @return Lista de todos los computadores
     */
    public List<Computer> getAllComputers() {
        return computerRepository.findAll();
    }

    /**
     * Obtiene un computador por su nombre.
     *
     * @param name Nombre del computador
     * @return Computer encontrado
     * @throws IllegalArgumentException Si el computador no existe
     */
    public Computer getComputerByName(String name) {
        return computerRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Computer not found: " + name));
    }

    /**
     * Obtiene un computador por su ID.
     *
     * @param id ID del computador
     * @return Computer encontrado
     * @throws IllegalArgumentException Si el computador no existe
     */
    public Computer getComputerById(Long id) {
        return computerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Computer not found: " + id));
    }

    /**
     * Actualiza el estado de un computador.
     * Usado cuando el agente C# reporta su estado.
     *
     * @param name Nombre del computador
     * @param status Nuevo estado
     * @return Computer actualizado
     */
    @Transactional
    public Computer updateComputerStatus(String name, Computer.ComputerStatus status) {
        Computer computer = getComputerByName(name);
        computer.setStatus(status);
        computer.setLastSeen(LocalDateTime.now());
        return computerRepository.save(computer);
    }

    /**
     * Actualiza la información de un computador.
     *
     * @param id ID del computador
     * @param ipAddress Nueva IP (opcional)
     * @param macAddress Nueva MAC (opcional)
     * @param labName Nuevo nombre de laboratorio (opcional)
     * @return Computer actualizado
     */
    @Transactional
    public Computer updateComputer(Long id, String ipAddress, String macAddress, String labName) {
        Computer computer = getComputerById(id);
        
        if (ipAddress != null) {
            computer.setIpAddress(ipAddress);
        }
        if (macAddress != null) {
            computer.setMacAddress(macAddress);
        }
        if (labName != null) {
            computer.setLabName(labName);
        }
        
        return computerRepository.save(computer);
    }

    /**
     * Elimina un computador del sistema.
     *
     * @param id ID del computador
     */
    @Transactional
    public void deleteComputer(Long id) {
        if (!computerRepository.existsById(id)) {
            throw new IllegalArgumentException("Computer not found: " + id);
        }
        computerRepository.deleteById(id);
    }
}

