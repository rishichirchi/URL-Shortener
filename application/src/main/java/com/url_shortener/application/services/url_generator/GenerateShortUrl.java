package com.url_shortener.application.services.url_generator;

import org.springframework.stereotype.Service;

@Service
public class GenerateShortUrl {
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String generateShortUrl(String id) {
        long numId;
        try {
            numId = Long.parseLong(id.replaceAll("\\D+", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format: " + id, e);
        }

        return base62Encode(numId);
    }

    private String base62Encode(long num) {
        if (num == 0)
            return "0";
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int rem = (int) (num % 62);
            sb.append(BASE62.charAt(rem));
            num /= 62;
        }
        return sb.reverse().toString();
    }
}
