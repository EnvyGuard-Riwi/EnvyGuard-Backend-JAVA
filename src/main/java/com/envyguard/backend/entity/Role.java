package com.envyguard.backend.entity;

/**
 * Enum que define los roles de usuario en el sistema.
 * ADMIN: Puede agregar, actualizar y eliminar usuarios.
 * OPERATOR: Solo puede manejar la página (sin gestión de usuarios).
 */
public enum Role {
    ADMIN,
    OPERATOR
}
