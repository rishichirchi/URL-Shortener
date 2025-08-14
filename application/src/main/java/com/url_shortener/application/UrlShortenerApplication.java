package com.url_shortener.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableScheduling
public class UrlShortenerApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
			.directory("./") // Look for .env in the current directory
			.ignoreIfMissing()
			.load();
		
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});

		// Debugging line to confirm the URI is loaded
		System.out.println("Attempting to connect with MONGODB_URI: " + System.getProperty("MONGODB_URI"));
		
		SpringApplication.run(UrlShortenerApplication.class, args);
	}
}
