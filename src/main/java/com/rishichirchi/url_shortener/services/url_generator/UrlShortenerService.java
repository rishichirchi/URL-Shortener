package com.rishichirchi.url_shortener.services.url_generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rishichirchi.url_shortener.services.storage.RedisStorageService;

@Service
public class UrlShortenerService {

    private final RedisStorageService redisStorageService;
    private final GenerateIDService generateIDService;
    private final GenerateShortUrl generateShortUrl;

    @Value("${shortener.base-url}")
    private String baseUrl;

    public UrlShortenerService(GenerateIDService generateIDService, GenerateShortUrl generateShortUrl, RedisStorageService redisStorageService) {
        this.generateIDService = generateIDService;
        this.generateShortUrl = generateShortUrl;
        this.redisStorageService = redisStorageService;
    }

    public String shorten(String longUrl) {
        String id = generateIDService.generateID();
        String shortCode = generateShortUrl.generateShortUrl(id);
        redisStorageService.saveUrl(shortCode, longUrl);
        return baseUrl + "/" + shortCode;
    }
}