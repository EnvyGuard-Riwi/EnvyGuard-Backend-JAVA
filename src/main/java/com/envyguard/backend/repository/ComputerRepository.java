package com.envyguard.backend.repository;

import com.envyguard.backend.entity.Computer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComputerRepository extends JpaRepository<Computer, Long> {
    Optional<Computer> findByName(String name);
    boolean existsByName(String name);
}