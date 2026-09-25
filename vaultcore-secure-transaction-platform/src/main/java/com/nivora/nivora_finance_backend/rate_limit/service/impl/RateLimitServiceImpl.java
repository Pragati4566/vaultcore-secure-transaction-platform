package com.nivora.nivora_finance_backend.rate_limit.service.impl;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.nivora.nivora_finance_backend.rate_limit.service.RateLimitService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RateLimitServiceImpl implements RateLimitService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean isAllowed(
            String key,
            int limit,
            long windowSeconds) {

        Long count = stringRedisTemplate
                .opsForValue()
                .increment(key);

        if (count != null && count == 1) {
            stringRedisTemplate.expire(
                    key,
                    Duration.ofSeconds(windowSeconds));
        }

        return count != null && count <= limit;
    }
}