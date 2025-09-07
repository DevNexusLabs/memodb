package com.spring.memodb.store;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class responsible for scheduling periodic cleanup of expired entries
 * in the Store.
 */
@Service
@Slf4j
public class StoreCleanupService {

    @Scheduled(fixedRate = 60000) // every 1 minute (milliseconds)
    public void scheduledCleanup() {
        log.debug("StoreCleanupService: Running scheduled cleanup of expired entries.");

        Store.getInstance().cleanupExpiredEntries();

        log.debug("StoreCleanupService: Expired entries cleaned up. Current store size: {}", Store.getInstance().size());
    }
}
