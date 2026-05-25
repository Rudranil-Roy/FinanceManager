package com.rudra.financemanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class for handling MVC level configurations.
 * Customizes cross-origin resource sharing (CORS) rules based on application properties.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigin;

    /**
     * Configures cross-origin resource sharing (CORS) mapping for all /api endpoints.
     * Restricts HTTP methods, allowed headers, and handles cookie credentials dynamically.
     *
     * @param registry CORS registry to apply mappings to.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigin.split(","))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
