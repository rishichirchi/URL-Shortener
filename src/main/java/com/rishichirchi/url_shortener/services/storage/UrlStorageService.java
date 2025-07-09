package com.rishichirchi.url_shortener.services.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.rishichirchi.url_shortener.models.UrlDocument;
import com.rishichirchi.url_shortener.repositories.UrlRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UrlStorageService {
    @Autowired
    private RedisStorageService redisStorageService;
    @Autowired
    private UrlRepository urlRepository;

    public void saveUrl(String shortCode, String longUrl) {
        UrlDocument document = new UrlDocument(shortCode, longUrl);
        urlRepository.save(document);
        redisStorageService.saveUrl(shortCode, longUrl);
    }

    public String getLongUrl(String shortCode) {
        // first ping redis cache
        // if not found, then query mongodb
        String cachedUrl = redisStorageService.getLongUrl(shortCode);
        if (cachedUrl != null) {
            return cachedUrl;
        }
        UrlDocument urlDocument = urlRepository.findByShortCode(shortCode);
        if (urlDocument != null) {
            redisStorageService.saveUrl(shortCode, urlDocument.getLongUrl());
            return urlDocument.getLongUrl();
        }

        return null;
    }

    public void incrementHits(String shortCode) {
        UrlDocument urlDocument = urlRepository.findByShortCode(shortCode);
        if (urlDocument == null) {
            return;
        }

        if (redisStorageService.getUrlHits(shortCode) < urlDocument.getClickCount()) {
            redisStorageService.setUrlHits(shortCode, urlDocument.getClickCount());
        }

        redisStorageService.incrementUrlHits(shortCode);
    }

    @Scheduled(fixedRate = 300000) // Sync every 5 minutes
    public void syncRedisClicksToMongoDB() {
        try {
            log.info("Syncing Redis click counts to MongoDB");
            Iterable<UrlDocument> allUrls = urlRepository.findAll();
            for (UrlDocument urlDocument : allUrls) {
                String shortCode = urlDocument.getShortCode();
                Long redisHits = redisStorageService.getUrlHits(shortCode);
                if (redisHits != null && redisHits > urlDocument.getClickCount()) {
                    urlDocument.setClickCount(redisHits);
                    urlRepository.save(urlDocument);
                    log.debug("Updated MongoDB for shortCode: {} with click count: {}", shortCode, redisHits);
                }
            }
            log.info("Successfully synced Redis click counts to MongoDB");

        } catch (Exception e) {
            log.error("Error syncing Redis click counts to MongoDB", e);
        }
    }

}
