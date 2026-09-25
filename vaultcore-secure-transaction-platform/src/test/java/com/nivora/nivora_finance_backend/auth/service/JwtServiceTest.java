package com.nivora.nivora_finance_backend.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.nivora.nivora_finance_backend.security.JwtService;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                "mySuperSecretKeyForNivoraFinanceApplicationJwt2026"
        );

        ReflectionTestUtils.setField(
                jwtService,
                "expiration",
                10800000L
        );
    }

    @Test
    void generateToken_ShouldCreateToken() {

        String token =
                jwtService.generateToken(
                        "test@example.com");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractEmail_ShouldReturnEmail() {

        String token =
                jwtService.generateToken(
                        "test@example.com");

        String email =
                jwtService.extractEmail(token);

        assertEquals(
                "test@example.com",
                email);
    }

    @Test
    void isTokenValid_ShouldReturnTrue() {

        String email =
                "test@example.com";

        String token =
                jwtService.generateToken(email);

        boolean valid =
                jwtService.isTokenValid(
                        token,
                        email);

        assertTrue(valid);
    }
}