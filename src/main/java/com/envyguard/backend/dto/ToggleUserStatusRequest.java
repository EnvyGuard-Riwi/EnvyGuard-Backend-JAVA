package com.envyguard.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para cambiar el estado de un usuario (enabled/disabled).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToggleUserStatusRequest {

    @NotNull(message = "El campo 'enabled' es requerido")
    private Boolean enabled;
}
