package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class
WebConfig {

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedHeaders("*")
                        .allowedMethods("*");
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Serve images from /app/maps/
                registry.addResourceHandler("/maps/**")
                        .addResourceLocations("file:/app/maps/");

                // This will allow Spring Boot to serve static files from the /static/ folder
                registry.addResourceHandler("/images/**")
                        .addResourceLocations("classpath:/images/");
                registry.addResourceHandler("/static/**")
                        .addResourceLocations("classpath:/static/");

                // Optional: Explicitly configure for css and js files (if needed)
                registry.addResourceHandler("/css/**")
                        .addResourceLocations("classpath:/static/");
                registry.addResourceHandler("/js/**")
                        .addResourceLocations("classpath:/static/");
            }
        };


    }



}
