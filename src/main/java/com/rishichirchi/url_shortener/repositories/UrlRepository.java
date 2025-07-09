package com.rishichirchi.url_shortener.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rishichirchi.url_shortener.models.UrlDocument;


public interface UrlRepository extends MongoRepository<UrlDocument, String>{
    UrlDocument findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
}
