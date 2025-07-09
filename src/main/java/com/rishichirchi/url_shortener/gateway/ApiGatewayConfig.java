package com.rishichirchi.url_shortener.gateway;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder){}
}
