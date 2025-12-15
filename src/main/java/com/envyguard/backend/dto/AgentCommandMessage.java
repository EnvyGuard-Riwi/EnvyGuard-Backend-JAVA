package com.envyguard.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for command messages sent to RabbitMQ in the format expected by C# agents.
 * 
 * FORMATO ESPERADO POR EL AGENTE:
 * 
 * Acciones normales:
 * {
 *   "action": "shutdown",
 *   "targetIp": "192.168.1.50",
 *   "parameters": ""
 * }
 * 
 * Wake-on-LAN (sin targetIp):
 * {
 *   "action": "wakeup",
 *   "targetIp": "",
 *   "macAddress": "08:bf:b8:03:13:6a"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentCommandMessage {

    /**
     * Action to execute on the remote computer.
     * Valid values: shutdown, reboot, wakeup, block_sites, unblock_sites, 
     * format, test, install_app, install_snap
     */
    private String action;

    /**
     * Target IP address of the computer.
     * Empty string for wakeup action.
     */
    private String targetIp;

    /**
     * MAC address (only for wakeup action).
     * This field is excluded from JSON when null.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String macAddress;

    /**
     * Additional parameters for the command.
     * Examples:
     * - "facebook.com" for block_sites/unblock_sites
     * - "git" for install_app
     * - "" for actions without parameters
     */
    @Builder.Default
    private String parameters = "";
}
