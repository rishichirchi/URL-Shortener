package com.rishichirchi.url_shortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;


@SpringBootApplication
public class UrlShortenerApplication {
	public static void main(String[] args) {
		SpringApplication.run(UrlShortenerApplication.class, args);
	}

}
