package com.url_shortener.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;

import com.url_shortener.gateway.filter.UrlValidationGatewayFilter;

import reactor.core.publisher.Mono;

@Configuration
public class ApiGatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("api_route", r -> r
                .path("/api/urls/**")
                .filters(f -> f
                    // .filter(new UrlValidationGatewayFilter())
                    .requestRateLimiter(config -> config
                        .setRateLimiter(apiRateLimiter())
                        .setKeyResolver(ipKeyResolver())
                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                .uri("http://localhost:8081"))
            
           
            .route("redirect_route", r -> r
                .path("/{shortCode}")
                .filters(f -> f
                    .requestRateLimiter(config -> config
                        .setRateLimiter(redirectRateLimiter())
                        .setKeyResolver(ipKeyResolver())
                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)))
                .uri("http://localhost:8081"))
            .build();
    }


    @Bean
    @Primary
    public RedisRateLimiter apiRateLimiter() {
        return new RedisRateLimiter(10, 20, 1);
    }

    @Bean
    public RedisRateLimiter redirectRateLimiter() {
        return new RedisRateLimiter(50, 100, 1);
    }


    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest().getRemoteAddress() != null ? 
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : 
                "unknown"
        );
    }
}
