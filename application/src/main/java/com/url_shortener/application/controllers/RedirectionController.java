package com.url_shortener.application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import com.url_shortener.application.services.storage.UrlStorageService;

@Controller
public class RedirectionController {
    @Autowired
    private UrlStorageService urlStorageService;

    @GetMapping("/{shortCode}")
    public RedirectView redirectToOriginalUrl(@PathVariable String shortCode) {
       String longUrl = urlStorageService.getLongUrl(shortCode);
        if (longUrl == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Short URL not found");
        }

        // Increment the click count
        urlStorageService.incrementHits(shortCode);

        // Redirect to the original URL
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(longUrl);
        return redirectView;
    }

}
