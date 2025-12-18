package com.envyguard.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComputerStatusDto {
    private String ipAddress;
    private String status;
    private String hostname; // Optional, in case agent sends it
    private boolean hasInternet = true;
}
