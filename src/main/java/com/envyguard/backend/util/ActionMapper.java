package com.envyguard.backend.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapper for converting backend action names to agent-compatible action names.
 * 
 * Backend uses uppercase with underscores (SHUTDOWN, WAKE_ON_LAN).
 * Agent expects lowercase (shutdown, wakeup).
 */
public class ActionMapper {

    private static final Map<String, String> ACTION_MAP = new HashMap<>();

    static {
        // Acciones de control de energía
        ACTION_MAP.put("SHUTDOWN", "shutdown");
        ACTION_MAP.put("REBOOT", "reboot");
        ACTION_MAP.put("WAKE_ON_LAN", "wakeup");
        
        // Acciones de bloqueo/desbloqueo
        ACTION_MAP.put("BLOCK_WEBSITE", "block_sites");
        ACTION_MAP.put("BLOCK_SITES", "block_sites");
        ACTION_MAP.put("UNBLOCK_WEBSITE", "unblock_sites");
        ACTION_MAP.put("UNBLOCK_SITES", "unblock_sites");
        
        // Acciones de instalación
        ACTION_MAP.put("INSTALL_APP", "install_app");
        ACTION_MAP.put("INSTALL_SNAP", "install_snap");
        
        // Otras acciones
        ACTION_MAP.put("FORMAT", "format");
        ACTION_MAP.put("TEST", "test");
        ACTION_MAP.put("LOCK_SESSION", "lock_session");
        ACTION_MAP.put("DISABLE_INTERNET", "disable_internet");
        ACTION_MAP.put("ENABLE_INTERNET", "enable_internet");
    }

    /**
     * Converts a backend action name to the agent-compatible format.
     * 
     * @param backendAction Action name from backend (e.g., "SHUTDOWN", "WAKE_ON_LAN")
     * @return Agent-compatible action name (e.g., "shutdown", "wakeup")
     * @throws IllegalArgumentException if action is not recognized
     */
    public static String toAgentAction(String backendAction) {
        if (backendAction == null || backendAction.trim().isEmpty()) {
            throw new IllegalArgumentException("Action cannot be null or empty");
        }

        String agentAction = ACTION_MAP.get(backendAction.toUpperCase());
        if (agentAction == null) {
            throw new IllegalArgumentException("Unknown action: " + backendAction + 
                ". Valid actions: " + ACTION_MAP.keySet());
        }

        return agentAction;
    }

    /**
     * Checks if an action is valid.
     * 
     * @param backendAction Action name to validate
     * @return true if the action exists in the mapping
     */
    public static boolean isValidAction(String backendAction) {
        return backendAction != null && ACTION_MAP.containsKey(backendAction.toUpperCase());
    }
}
