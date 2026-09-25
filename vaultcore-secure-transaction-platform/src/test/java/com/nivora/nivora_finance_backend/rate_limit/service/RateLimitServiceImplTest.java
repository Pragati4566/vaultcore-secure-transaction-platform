package com.nivora.nivora_finance_backend.rate_limit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.nivora.nivora_finance_backend.rate_limit.service.impl.RateLimitServiceImpl;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceImplTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RateLimitServiceImpl rateLimitService;

    private final String key = "rate_limit:login:127.0.0.1";
    private final int limit = 5;
    private final long windowSeconds = 60;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void shouldAllowRequestWhenCountIsBelowLimit() {

        when(valueOperations.increment(key)).thenReturn(3L);

        boolean allowed = rateLimitService.isAllowed(
                key,
                limit,
                windowSeconds);

        assertTrue(allowed);

        verify(valueOperations).increment(key);
    }

    @Test
    void shouldAllowRequestWhenCountEqualsLimit() {

        when(valueOperations.increment(key)).thenReturn(5L);

        boolean allowed = rateLimitService.isAllowed(
                key,
                limit,
                windowSeconds);

        assertTrue(allowed);

        verify(valueOperations).increment(key);
    }

    @Test
    void shouldBlockRequestWhenCountExceedsLimit() {

        when(valueOperations.increment(key)).thenReturn(6L);

        boolean allowed = rateLimitService.isAllowed(
                key,
                limit,
                windowSeconds);

        assertFalse(allowed);

        verify(valueOperations).increment(key);
    }

    @Test
    void shouldSetExpirationOnFirstRequest() {

        when(valueOperations.increment(key)).thenReturn(1L);

        rateLimitService.isAllowed(
                key,
                limit,
                windowSeconds);

        verify(stringRedisTemplate)
                .expire(key, Duration.ofSeconds(windowSeconds));
    }

    @Test
    void shouldReturnFalseWhenIncrementReturnsNull() {

        when(valueOperations.increment(key)).thenReturn(null);

        boolean allowed = rateLimitService.isAllowed(
                key,
                limit,
                windowSeconds);

        assertFalse(allowed);

        verify(valueOperations).increment(key);
        verify(stringRedisTemplate, never())
                .expire(anyString(), any(Duration.class));
    }
}