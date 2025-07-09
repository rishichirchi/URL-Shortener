package com.rishichirchi.url_shortener.services.url_generator;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GenerateIDService {
    private final String machinePrefix = "A1";
    private long sequence = 0;

    public synchronized String generateID(){
        if(sequence >= 999999) {
            sequence = 0; // Reset sequence if it exceeds 6 digits
        }
        sequence++;
        String id = machinePrefix + String.format("%06d", sequence);

        log.info("Generated ID: {}", id);
        return id;
    }
}
