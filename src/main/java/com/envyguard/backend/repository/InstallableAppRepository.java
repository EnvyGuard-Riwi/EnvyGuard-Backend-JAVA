package com.envyguard.backend.repository;

import com.envyguard.backend.entity.InstallableApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstallableAppRepository extends JpaRepository<InstallableApp, Long> {
    Optional<InstallableApp> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
