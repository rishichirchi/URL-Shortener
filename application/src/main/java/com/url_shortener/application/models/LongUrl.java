package com.url_shortener.application.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LongUrl {
    private String longUrl;

    public LongUrl(String longUrl){
        this.longUrl = longUrl;
    }
}
