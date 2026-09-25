package com.nivora.nivora_finance_backend.rate_limit.service;

public interface RateLimitService {

    boolean isAllowed(
            String key,
            int limit,
            long windowSeconds);
}