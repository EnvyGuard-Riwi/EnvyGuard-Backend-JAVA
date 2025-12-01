package com.envyguard.envyguard_backend.repository;

import com.envyguard.envyguard_backend.entity.Computer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ComputerRepository extends JpaRepository<Computer, Long> {
    Optional<Computer> findByName(String name);
    Optional<Computer> findByMacAddress(String macAddress);
    boolean existsByName(String name);
}
