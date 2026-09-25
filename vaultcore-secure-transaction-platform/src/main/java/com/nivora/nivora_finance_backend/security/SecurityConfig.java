package com.nivora.nivora_finance_backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nivora.nivora_finance_backend.rate_limit.filter.RateLimitFilter;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        @Value("${cors.allowed-origin}")
        private String allowedOrigin;

        private final JwtFilter jwtFilter;
        private final RateLimitFilter rateLimitFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                return http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())

                                .authorizeHttpRequests(auth -> auth

                                                .requestMatchers(
                                                                "/docs",
                                                                "/docs.html",
                                                                "/swagger/**",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",

                                                                "/api/v1/auth/signup",
                                                                "/api/v1/auth/login",
                                                                "/api/v1/auth/verify-otp",

                                                                "/error",
                                                                "/favicon.ico")
                                                .permitAll()

                                                .anyRequest().authenticated())

                                .addFilterBefore(
                                                jwtFilter,
                                                UsernamePasswordAuthenticationFilter.class)
                                .addFilterAfter(rateLimitFilter, JwtFilter.class)
                                .build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(
                                List.of(allowedOrigin));

                configuration.setAllowedMethods(
                                List.of(
                                                "GET",
                                                "POST",
                                                "PUT",
                                                "DELETE",
                                                "OPTIONS"));

                configuration.setAllowedHeaders(
                                List.of("*"));

                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", configuration);

                return source;
        }
}