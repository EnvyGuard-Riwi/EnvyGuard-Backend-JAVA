package com.envyguard.backend.repository;

import com.envyguard.backend.entity.Sala2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Sala2 entity operations.
 */
@Repository
public interface Sala2Repository extends JpaRepository<Sala2, Long> {
    
    /**
     * Find a computer by its ID in Sala 2.
     *
     * @param id Computer ID
     * @return Optional containing the computer if found
     */
    Optional<Sala2> findById(Long id);
}
