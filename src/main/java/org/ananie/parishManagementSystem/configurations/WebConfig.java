package org.ananie.parishManagementSystem.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Allow all endpoints
                .allowedOrigins("http://localhost:3000")  // Your Vite frontend URL
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Include OPTIONS
                .allowedHeaders("*")  // Allow all headers
                .allowCredentials(true);  // Allow cookies if needed
    }
}