package com.envyguard.backend.service;

import com.envyguard.backend.dto.IncidentRequest;
import com.envyguard.backend.dto.IncidentResponse;
import com.envyguard.backend.entity.Incident;
import com.envyguard.backend.entity.IncidentStatus;
import com.envyguard.backend.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing security incidents/news.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IncidentService {

    private final IncidentRepository incidentRepository;

    /**
     * Creates a new incident.
     *
     * @param request Incident creation request
     * @return Created incident response
     */
    @Transactional
    public IncidentResponse create(IncidentRequest request) {
        Incident incident = Incident.builder()
                .description(request.getDescription())
                .severity(request.getSeverity())
                .status(IncidentStatus.PENDING)
                .build();

        Incident saved = incidentRepository.save(incident);
        log.info("Created new incident with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    /**
     * Finds all incidents.
     *
     * @return List of all incidents
     */
    public List<IncidentResponse> findAll() {
        return incidentRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Finds all incidents by status.
     *
     * @param status Incident status (PENDING/COMPLETED)
     * @return List of incidents
     */
    public List<IncidentResponse> findAllByStatus(IncidentStatus status) {
        return incidentRepository.findAllByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Marks an incident as completed.
     *
     * @param id Incident ID
     * @return Updated incident response
     */
    @Transactional
    public IncidentResponse markAsCompleted(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found"));

        incident.setStatus(IncidentStatus.COMPLETED);
        incident.setCompletedAt(LocalDateTime.now());

        Incident saved = incidentRepository.save(incident);
        log.info("Marked incident {} as COMPLETED", id);

        return mapToResponse(saved);
    }

    /**
     * Scheduled task to delete completed incidents older than 48 hours.
     * Runs every hour to check for candidates.
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    @Transactional
    public void deleteOldCompletedIncidents() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(48);
        List<Incident> oldIncidents = incidentRepository.findAllByStatusAndCompletedAtBefore(IncidentStatus.COMPLETED,
                threshold);

        if (!oldIncidents.isEmpty()) {
            incidentRepository.deleteAll(oldIncidents);
            log.info("Deleted {} old completed incidents", oldIncidents.size());
        }
    }

    private IncidentResponse mapToResponse(Incident incident) {
        return IncidentResponse.builder()
                .id(incident.getId())
                .description(incident.getDescription())
                .severity(incident.getSeverity())
                .status(incident.getStatus())
                .createdAt(incident.getCreatedAt())
                .completedAt(incident.getCompletedAt())
                .build();
    }
}
