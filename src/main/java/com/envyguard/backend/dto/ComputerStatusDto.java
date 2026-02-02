package com.envyguard.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class ComputerStatusDto {
    @com.fasterxml.jackson.annotation.JsonAlias("IpAddress")
    private String ipAddress;

    @com.fasterxml.jackson.annotation.JsonAlias("Status")
    private String status;

    @com.fasterxml.jackson.annotation.JsonAlias({ "PcName", "PcId" })
    private String hostname; // Optional, in case agent sends it
}
