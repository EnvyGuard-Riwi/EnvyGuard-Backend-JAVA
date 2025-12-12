package com.envyguard.backend.repository;

import com.envyguard.backend.entity.Sala1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Sala1 entity operations.
 */
@Repository
public interface Sala1Repository extends JpaRepository<Sala1, Long> {
    
    /**
     * Find a computer by its ID in Sala 1.
     *
     * @param id Computer ID
     * @return Optional containing the computer if found
     */
    Optional<Sala1> findById(Long id);
}
