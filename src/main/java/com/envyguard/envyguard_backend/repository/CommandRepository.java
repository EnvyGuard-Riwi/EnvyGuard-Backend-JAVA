package com.envyguard.envyguard_backend.repository;

import com.envyguard.envyguard_backend.entity.Command;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long> {
    List<Command> findByComputerName(String computerName);
    List<Command> findByStatus(String status);
}
