package com.url_shortener.application.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.url_shortener.application.models.LongUrl;
import com.url_shortener.application.services.url_generator.UrlShortenerService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/urls")
public class ApiController {
   private final UrlShortenerService urlShortenerService;

    public ApiController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@RequestBody LongUrl longUrl) {
        if (longUrl == null || longUrl.getLongUrl() == null || longUrl.getLongUrl().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid URL provided");
        }

        String shortUrl = urlShortenerService.shorten(longUrl.getLongUrl());

        Map<String, String> responseBody = Map.of(
                "shortUrl", shortUrl,
                "longUrl", longUrl.getLongUrl()
        );
        ResponseEntity<Map<String, String>> response = ResponseEntity.ok()
                .header("Location", shortUrl)
                .body(responseBody);

       return response;
    }

}
