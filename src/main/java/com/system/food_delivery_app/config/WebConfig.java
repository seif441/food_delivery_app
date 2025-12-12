package com.system.food_delivery_app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow all endpoints
                // FIXED: Use allowedOriginPatterns instead of allowedOrigins
                .allowedOriginPatterns(
                    "http://127.0.0.1:5500", 
                    "http://localhost:5500", 
                    "http://localhost:8080",
                    "http://localhost:5005"  // Added your current port
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600); // Cache preflight requests for 1 hour
    }
}