package com.envyguard.backend.service;

import com.envyguard.backend.entity.InstallableApp;
import com.envyguard.backend.repository.InstallableAppRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstallableAppService {

    private final InstallableAppRepository installableAppRepository;

    public List<InstallableApp> findAll() {
        return installableAppRepository.findAll();
    }

    public Optional<InstallableApp> findByName(String name) {
        return installableAppRepository.findByNameIgnoreCase(name);
    }

    @Transactional
    public InstallableApp save(InstallableApp app) {
        if (app.getId() == null && installableAppRepository.existsByNameIgnoreCase(app.getName())) {
            throw new IllegalArgumentException("App with name " + app.getName() + " already exists");
        }
        return installableAppRepository.save(app);
    }

    @Transactional
    public void delete(Long id) {
        installableAppRepository.deleteById(id);
    }

    public String getCommandForApp(String appName) {
        return installableAppRepository.findByNameIgnoreCase(appName)
                .map(InstallableApp::getCommand)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + appName));
    }
}
