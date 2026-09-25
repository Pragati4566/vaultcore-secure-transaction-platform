package com.nivora.nivora_finance_backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Nivora Finance API",
                version = "1.0.0",
                description = """
                        Nivora Finance is a Digital Wallet Backend Platform built using Java 21,
                        Spring Boot, PostgreSQL, Redis and Docker.

                        Core Features:
                        • JWT Authentication
                        • OTP Email Verification
                        • Wallet Management
                        • Money Transfers
                        • Transaction History
                        • Redis Session Management
                        • Email Notifications

                        Engineering Features:
                        • Idempotent Transactions
                        • Concurrency Protection (Pessimistic Locking)
                        • Dockerized Deployment
                        • OpenAPI Documentation
                        • Swagger UI
                        • Redoc Documentation

                        Tech Stack:
                        • Java 21
                        • Spring Boot
                        • PostgreSQL
                        • Redis
                        • Docker
                        • JWT
                        • OpenAPI 3
                        """
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}