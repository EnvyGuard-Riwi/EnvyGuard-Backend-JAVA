package com.envyguard.backend.repository;

import com.envyguard.backend.entity.Sala3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Sala3 entity operations.
 */
@Repository
public interface Sala3Repository extends JpaRepository<Sala3, Long> {
    
    /**
     * Find a computer by its ID in Sala 3.
     *
     * @param id Computer ID
     * @return Optional containing the computer if found
     */
    Optional<Sala3> findById(Long id);
}
