package com.url_shortener.gateway.filter;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class UrlValidationGatewayFilter implements GatewayFilter {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        if (shouldValidateRequest(request)) {
            return validateRequestBody(exchange, chain);
        }
        
        return chain.filter(exchange);
    }
    

    private boolean shouldValidateRequest(ServerHttpRequest request) {
        return HttpMethod.POST.equals(request.getMethod()) && 
               "/api/urls/shorten".equals(request.getURI().getPath()) &&
               request.getHeaders().getContentType() != null &&
               request.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON);
    }
    

    private Mono<Void> validateRequestBody(ServerWebExchange exchange, GatewayFilterChain chain) {
        return DataBufferUtils.join(exchange.getRequest().getBody())
            .flatMap(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);
                
                String requestBody = new String(bytes, StandardCharsets.UTF_8);
                
                try {
                
                    JsonNode node = objectMapper.readTree(requestBody);
                    
                    if (!node.has("longUrl")) {
                        return sendErrorResponse(exchange, "Missing 'longUrl' field");
                    }
                    
                    String url = node.get("longUrl").asText();
                    if (!isValidUrl(url)) {
                        return sendErrorResponse(exchange, "Invalid URL format. URL must start with http:// or https://");
                    }
                    
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate().build();
                    
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    
                } catch (JsonProcessingException e) {
                    return sendErrorResponse(exchange, "Invalid JSON format");
                }
            });
    }
    

    private boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        try {
            URI uri = new URI(url);
            return (url.startsWith("http://") || url.startsWith("https://")) &&
                   uri.getHost() != null && !uri.getHost().isEmpty();
        } catch (URISyntaxException e) {
            return false;
        }
    }
    

    private Mono<Void> sendErrorResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        try {
            String jsonError = objectMapper.writeValueAsString(
                    java.util.Collections.singletonMap("error", message));
            
            DataBuffer buffer = response.bufferFactory().wrap(jsonError.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Flux.just(buffer));
        } catch (JsonProcessingException e) {
            return response.setComplete();
        }
    }
}
