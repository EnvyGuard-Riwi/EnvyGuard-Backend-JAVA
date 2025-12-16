package com.envyguard.backend.controller;

import com.envyguard.backend.entity.InstallableApp;
import com.envyguard.backend.service.InstallableAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/installable-apps")
@RequiredArgsConstructor
@Tag(name = "Installable Apps", description = "Management of available applications for remote installation")
public class InstallableAppController {

    private final InstallableAppService installableAppService;

    @Operation(summary = "List all installable apps", description = "Returns the list of applications available for installation.")
    @GetMapping
    public ResponseEntity<List<InstallableApp>> findAll() {
        return ResponseEntity.ok(installableAppService.findAll());
    }

    @Operation(summary = "Create a new installable app", description = "Defines a new application with its installation command.")
    @PostMapping
    public ResponseEntity<InstallableApp> create(@RequestBody InstallableApp app) {
        return ResponseEntity.status(HttpStatus.CREATED).body(installableAppService.save(app));
    }

    @Operation(summary = "Delete an installable app", description = "Removes an application from the available list.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        installableAppService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
