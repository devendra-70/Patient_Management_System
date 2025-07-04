package com.pm.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF since this is a stateless REST API
                .csrf(csrf -> csrf.disable())

                // Configure URL authorization
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(
                                "/login",
                                "/validate",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Any other endpoint needs to be authenticated
                        .anyRequest().authenticated()
                )

                // Enable HTTP Basic for any fallback
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
