package com.envyguard.backend.repository;

import com.envyguard.backend.entity.Incident;
import com.envyguard.backend.entity.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findAllByStatusOrderByCreatedAtDesc(IncidentStatus status);

    List<Incident> findAllByOrderByCreatedAtDesc();

    List<Incident> findAllByStatusAndCompletedAtBefore(IncidentStatus status, LocalDateTime completedAt);
}
