package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS (make sure you have a CorsConfig bean if needed)
                .cors(Customizer.withDefaults())
                // For REST APIs, disabling CSRF is common (ensure this fits your needs)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Allow unauthenticated access to /api/hello
                        .requestMatchers("/api/hello").permitAll()
                        // Permit any public endpoints you need
                        // .antMatchers("/public/**").permitAll()
                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                // Use basic auth for endpoints that require authentication
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}