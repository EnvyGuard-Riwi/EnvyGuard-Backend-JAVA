package com.envyguard.backend.controller;

import com.envyguard.backend.entity.BlockedWebsite;
import com.envyguard.backend.service.BlockedWebsiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/blocked-websites")
@RequiredArgsConstructor
@Tag(name = "Blocked Websites", description = "Management of globally blocked websites")
public class BlockedWebsiteController {

    private final BlockedWebsiteService blockedWebsiteService;

    @Operation(summary = "List all blocked websites", description = "Returns a list of all sites currently blocked directly or via global policy.")
    @GetMapping
    public ResponseEntity<List<BlockedWebsite>> findAll() {
        return ResponseEntity.ok(blockedWebsiteService.findAll());
    }

    @Operation(summary = "Count blocked websites", description = "Returns the total number of blocked websites.")
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> count() {
        return ResponseEntity.ok(Map.of("count", blockedWebsiteService.count()));
    }

    @Operation(summary = "Add a blocked website", description = "Adds a website to the block list and immediately broadcasts the block command to ALL computers.")
    @PostMapping
    public ResponseEntity<BlockedWebsite> add(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String url = request.get("url");

        if (name == null || url == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(blockedWebsiteService.add(name, url));
    }

    @Operation(summary = "Remove a blocked website", description = "Removes a website from the block list and broadcasts the unblock command to ALL computers.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        blockedWebsiteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
