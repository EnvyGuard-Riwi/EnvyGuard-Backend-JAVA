package com.envyguard.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar usuarios. Todos los campos son opcionales; los que vengan se actualizarán.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @Email
    private String email;

    @Size(min = 6, max = 100)
    private String password;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    private Boolean enabled;
}
