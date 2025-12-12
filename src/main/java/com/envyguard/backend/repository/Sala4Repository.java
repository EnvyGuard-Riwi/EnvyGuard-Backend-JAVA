package com.envyguard.backend.repository;

import com.envyguard.backend.entity.Sala4;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Sala4 entity operations.
 */
@Repository
public interface Sala4Repository extends JpaRepository<Sala4, Long> {
    
    /**
     * Find a computer by its ID in Sala 4.
     *
     * @param id Computer ID
     * @return Optional containing the computer if found
     */
    Optional<Sala4> findById(Long id);
}
