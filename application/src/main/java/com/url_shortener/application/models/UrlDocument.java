package com.url_shortener.application.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Document(collection = "urls")
@Getter
@Setter
@NoArgsConstructor
public class UrlDocument {
    @Id
    private String id;
    private String shortCode;
    private String longUrl;
    private LocalDateTime createdAt;
    private long clickCount;

    public UrlDocument(String shortCode, String longUrl) {
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.createdAt = LocalDateTime.now();
        this.clickCount = 0;
    }

    public UrlDocument(String id, String shortCode, String longUrl, LocalDateTime createdAt, int clickCount) {
        this.id = id;
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.createdAt = createdAt;
        this.clickCount = clickCount;
    }
}
