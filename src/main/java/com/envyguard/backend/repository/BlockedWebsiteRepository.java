package com.envyguard.backend.repository;

import com.envyguard.backend.entity.BlockedWebsite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedWebsiteRepository extends JpaRepository<BlockedWebsite, Long> {
    Optional<BlockedWebsite> findByUrl(String url);

    boolean existsByUrl(String url);
}
