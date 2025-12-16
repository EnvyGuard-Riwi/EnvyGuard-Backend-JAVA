package com.envyguard.backend.service;

import com.envyguard.backend.dto.IncidentResponse;
import com.envyguard.backend.entity.Incident;
import com.envyguard.backend.entity.IncidentStatus;
import com.envyguard.backend.entity.Severity;
import com.envyguard.backend.repository.IncidentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private IncidentService incidentService;

    @Test
    void markAsCompleted_ShouldUpdateStatus() {
        // Arrange
        Long incidentId = 1L;
        Incident incident = Incident.builder()
                .id(incidentId)
                .status(IncidentStatus.PENDING)
                .severity(Severity.HIGH)
                .description("Test incident")
                .build();

        when(incidentRepository.findById(incidentId)).thenReturn(Optional.of(incident));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        IncidentResponse response = incidentService.markAsCompleted(incidentId);

        // Assert
        assertEquals(IncidentStatus.COMPLETED, response.getStatus());
        assertNotNull(response.getCompletedAt());
    }
}
