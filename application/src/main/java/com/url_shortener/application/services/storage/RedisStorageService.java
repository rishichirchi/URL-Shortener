package com.url_shortener.application.services.storage;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisStorageService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void saveUrl(String shortCode, String longUrl) {
        redisTemplate.opsForValue().set(shortCode, longUrl, 30, TimeUnit.DAYS);
    }

    public String getLongUrl(String shortCode) {
        return redisTemplate.opsForValue().get(shortCode);
    }

    public void incrementUrlHits(String shortCode) {
        redisTemplate.opsForValue().increment(shortCode + ":hits");
    }

    public Long getUrlHits(String shortCode) {
        String value = redisTemplate.opsForValue().get(shortCode + ":hits");
        return value != null ? Long.parseLong(value) : null;
    }

    public boolean hasHitCounter(String shortCode) {
        return redisTemplate.hasKey(shortCode + ":hits");
    }

    public void setUrlHits(String shortCode, long count) {
        redisTemplate.opsForValue().set(shortCode + ":hits", String.valueOf(count));
    }
}
