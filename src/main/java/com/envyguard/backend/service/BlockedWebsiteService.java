package com.envyguard.backend.service;

import com.envyguard.backend.dto.CommandRequest;
import com.envyguard.backend.entity.BlockedWebsite;
import com.envyguard.backend.entity.Sala1;
import com.envyguard.backend.entity.Sala2;
import com.envyguard.backend.entity.Sala3;
import com.envyguard.backend.entity.Sala4;
import com.envyguard.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockedWebsiteService {

    private final BlockedWebsiteRepository blockedWebsiteRepository;
    private final CommandService commandService;
    private final Sala1Repository sala1Repository;
    private final Sala2Repository sala2Repository;
    private final Sala3Repository sala3Repository;
    private final Sala4Repository sala4Repository;

    public List<BlockedWebsite> findAll() {
        return blockedWebsiteRepository.findAll();
    }

    public long count() {
        return blockedWebsiteRepository.count();
    }

    @Transactional
    public BlockedWebsite add(String name, String url) {
        if (blockedWebsiteRepository.existsByUrl(url)) {
            throw new IllegalArgumentException("Website URL is already blocked: " + url);
        }

        BlockedWebsite website = BlockedWebsite.builder()
                .name(name)
                .url(url)
                .build();

        BlockedWebsite saved = blockedWebsiteRepository.save(website);
        log.info("Added blocked website: {}", url);

        broadcastCommand("BLOCK_WEBSITE", url);

        return saved;
    }

    @Transactional
    public void delete(Long id) {
        BlockedWebsite website = blockedWebsiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blocked website not found with ID: " + id));

        String url = website.getUrl();
        blockedWebsiteRepository.delete(website);
        log.info("Removed blocked website: {}", url);

        broadcastCommand("UNBLOCK_WEBSITE", url);
    }

    private void broadcastCommand(String action, String url) {
        log.info("Broadcasting {} for {} to all computers...", action, url);

        // Room 1
        List<Sala1> sala1 = sala1Repository.findAll();
        for (Sala1 pc : sala1) {
            sendCommandSafely(1, pc.getId(), action, url);
        }

        // Room 2
        List<Sala2> sala2 = sala2Repository.findAll();
        for (Sala2 pc : sala2) {
            sendCommandSafely(2, pc.getId(), action, url);
        }

        // Room 3
        List<Sala3> sala3 = sala3Repository.findAll();
        for (Sala3 pc : sala3) {
            sendCommandSafely(3, pc.getId(), action, url);
        }

        // Room 4
        List<Sala4> sala4 = sala4Repository.findAll();
        for (Sala4 pc : sala4) {
            sendCommandSafely(4, pc.getId(), action, url);
        }

        log.info("Broadcast completed.");
    }

    private void sendCommandSafely(Integer salaNumber, Long pcId, String action, String url) {
        try {
            CommandRequest request = new CommandRequest(salaNumber, pcId, action, url);
            commandService.createCommand(request);
        } catch (Exception e) {
            log.error("Failed to send {} command to Room {} PC {}: {}", action, salaNumber, pcId, e.getMessage());
        }
    }
}
