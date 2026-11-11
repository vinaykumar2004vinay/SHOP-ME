package com.ecommerce.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CorsConfig — allows the frontend (running on a different port) to call the backend API.
 * Without this, browsers block cross-origin requests due to the Same-Origin Policy.
 *
 * Frontend runs on: http://127.0.0.1:5500 (VS Code Live Server) or port 3000
 * Backend runs on:  http://localhost:8080
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(
                                "http://localhost:3000",
                                "http://127.0.0.1:3000",
                                "http://127.0.0.1:5500",  // VS Code Live Server default
                                "http://localhost:5500"
                        )
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
