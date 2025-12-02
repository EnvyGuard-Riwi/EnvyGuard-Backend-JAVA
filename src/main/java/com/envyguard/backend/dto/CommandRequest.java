package com.envyguard.backend.dto;

import com.envyguard.backend.entity.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitudes de comandos remotos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandRequest {

    @NotBlank(message = "Computer name is required")
    private String computerName;

    @NotNull(message = "Command type is required")
    private Command.CommandType commandType;

    private String parameters;
}
