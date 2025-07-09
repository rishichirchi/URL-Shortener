package com.rishichirchi.url_shortener.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortUrl {
    private String shortUrl;

    public ShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}
