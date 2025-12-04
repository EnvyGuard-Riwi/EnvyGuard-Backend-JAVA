package com.envyguard.backend.service;

import com.envyguard.backend.entity.Computer;
import com.envyguard.backend.repository.ComputerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing computers in the system.
 */
@Service
@RequiredArgsConstructor
public class ComputerService {

    private final ComputerRepository computerRepository;

    /**
     * Creates a new computer in the system.
     *
     * @param name Unique computer name
     * @param ipAddress IP address (optional)
     * @param macAddress MAC address (optional)
     * @param labName Laboratory name (optional)
     * @return Created computer
     * @throws IllegalArgumentException If name already exists
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
     * Gets all computers.
     *
     * @return List of all computers
     */
    public List<Computer> getAllComputers() {
        return computerRepository.findAll();
    }

    /**
     * Gets a computer by its name.
     *
     * @param name Computer name
     * @return Found computer
     * @throws IllegalArgumentException If computer does not exist
     */
    public Computer getComputerByName(String name) {
        return computerRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Computer not found: " + name));
    }

    /**
     * Gets a computer by its ID.
     *
     * @param id Computer ID
     * @return Found computer
     * @throws IllegalArgumentException If computer does not exist
     */
    public Computer getComputerById(Long id) {
        return computerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Computer not found: " + id));
    }

    /**
     * Updates a computer's status.
     * Used when the C# agent reports its status.
     *
     * @param name Computer name
     * @param status New status
     * @return Updated computer
     */
    @Transactional
    public Computer updateComputerStatus(String name, Computer.ComputerStatus status) {
        Computer computer = getComputerByName(name);
        computer.setStatus(status);
        computer.setLastSeen(LocalDateTime.now());
        return computerRepository.save(computer);
    }

    /**
     * Updates computer information.
     *
     * @param id Computer ID
     * @param ipAddress New IP (optional)
     * @param macAddress New MAC (optional)
     * @param labName New laboratory name (optional)
     * @return Updated computer
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
     * Deletes a computer from the system.
     *
     * @param id Computer ID
     */
    @Transactional
    public void deleteComputer(Long id) {
        if (!computerRepository.existsById(id)) {
            throw new IllegalArgumentException("Computer not found: " + id);
        }
        computerRepository.deleteById(id);
    }
}

