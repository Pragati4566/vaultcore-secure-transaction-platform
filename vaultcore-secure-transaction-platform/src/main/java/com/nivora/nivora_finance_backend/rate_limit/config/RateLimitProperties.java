package com.nivora.nivora_finance_backend.rate_limit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "app.rate-limit")
@Getter
@Setter
public class RateLimitProperties {

    private Rule login = new Rule();
    private Rule signup = new Rule();
    private Rule verifyOtp = new Rule();
    private Rule transfer = new Rule();
    private Rule qrPayment = new Rule();

    @Getter
    @Setter
    public static class Rule {
        private int limit;
        private long windowSeconds;
    }
}
