package com.url_shortener.application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.url_shortener.application.repositories.UrlRepository;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private UrlRepository urlRepository;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        
      
        try {
            redisTemplate.opsForValue().get("health-check");
            health.put("redis", "UP");
        } catch (Exception e) {
            health.put("redis", "DOWN - " + e.getMessage());
        }
        
      
        try {
            urlRepository.count();
            health.put("mongodb", "UP");
        } catch (Exception e) {
            health.put("mongodb", "DOWN - " + e.getMessage());
        }
        
        return ResponseEntity.ok(health);
    }
}
