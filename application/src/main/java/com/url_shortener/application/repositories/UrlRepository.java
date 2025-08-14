package com.url_shortener.application.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.url_shortener.application.models.UrlDocument;


public interface UrlRepository extends MongoRepository<UrlDocument, String>{
    UrlDocument findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
}
