package com.envyguard.backend.controller;

import com.envyguard.backend.dto.ToggleUserStatusRequest;
import com.envyguard.backend.dto.UpdateUserRequest;
import com.envyguard.backend.dto.UserResponse;
import com.envyguard.backend.service.AuthService;
import com.envyguard.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User management controller.
 * Only users with ADMIN role can manage users.
 */
@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User Management", description = "User management API - ADMIN only")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    /**
     * Get all users. ADMIN only.
     */
    @Operation(summary = "Get all users", description = "Retrieves complete list of all users. Only accessible by ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User list retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    /**
     * Update user. ADMIN only.
     */
    @Operation(summary = "Update user", description = "Updates user information including email, password, name, role, and status. Only accessible by ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid data or email already exists"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "User ID", example = "1") @PathVariable Long id, 
            @Valid @RequestBody UpdateUserRequest req) {
        try {
            UserResponse updated = userService.updateUser(id, req);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Delete user. ADMIN only.
     */
    @Operation(summary = "Delete user", description = "Permanently deletes a user from the system. Only accessible by ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "User ID", example = "1") @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().body("Usuario eliminado correctamente");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Toggle user enabled status (enable/disable). ADMIN only.
     * 
     * @param id      User ID
     * @param request Object with new status (enabled: true/false)
     * @return Updated user
     */
    @Operation(summary = "Enable/Disable user", description = "Changes user account status. Disabled users cannot login or access the system. Only accessible by ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User status updated successfully", content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid data"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserStatus(
            @Parameter(description = "User ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody ToggleUserStatusRequest request) {
        try {
            UserResponse updated = userService.toggleUserStatus(id, request.getEnabled());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
