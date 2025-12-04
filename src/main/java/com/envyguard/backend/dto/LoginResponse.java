package com.envyguard.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuestas de autenticación exitosa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private String email;
    private String firstName;
    private String lastName;
    @Builder.Default
    private String type = "Bearer";
}
