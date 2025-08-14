package com.url_shortener.application.models;

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
