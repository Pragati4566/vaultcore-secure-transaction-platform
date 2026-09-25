package com.nivora.nivora_finance_backend.rate_limit.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nivora.nivora_finance_backend.auth.entity.User;
import com.nivora.nivora_finance_backend.common.exception.RateLimitExceededException;
import com.nivora.nivora_finance_backend.common.exception.UnauthorizedException;
import com.nivora.nivora_finance_backend.common.handler.FilterExceptionHandler;
import com.nivora.nivora_finance_backend.rate_limit.config.RateLimitProperties;
import com.nivora.nivora_finance_backend.rate_limit.service.RateLimitService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final FilterExceptionHandler filterExceptionHandler;
    private final RateLimitProperties rateLimitConfig;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {

            String path = request.getRequestURI();

            // Public APIs (Rate limit by IP)
            if (path.equals("/api/v1/auth/login")) {

                String key = "rate_limit:login:" + request.getRemoteAddr();

                checkRateLimit(
                        key,
                        rateLimitConfig.getLogin().getLimit(),
                        rateLimitConfig.getLogin().getWindowSeconds());

            } else if (path.equals("/api/v1/auth/signup")) {

                String key = "rate_limit:signup:" + request.getRemoteAddr();

                checkRateLimit(
                        key,
                        rateLimitConfig.getSignup().getLimit(),
                        rateLimitConfig.getSignup().getWindowSeconds());

            } else if (path.equals("/api/v1/auth/verify-otp")) {

                String key = "rate_limit:verify_otp:" + request.getRemoteAddr();

                checkRateLimit(
                        key,
                        rateLimitConfig.getVerifyOtp().getLimit(),
                        rateLimitConfig.getVerifyOtp().getWindowSeconds());

            }

            // Protected APIs (Rate limit by User)
            else if (path.equals("/api/v1/transactions/transfer")) {

                User user = getAuthenticatedUser();

                String key = "rate_limit:transfer:user:" + user.getId();

                checkRateLimit(
                        key,
                        rateLimitConfig.getTransfer().getLimit(),
                        rateLimitConfig.getTransfer().getWindowSeconds());

            } else if (path.equals("/api/v1/qr/pay")) {

                User user = getAuthenticatedUser();

                String key = "rate_limit:qr_payment:user:" + user.getId();

                checkRateLimit(
                        key,
                        rateLimitConfig.getQrPayment().getLimit(),
                        rateLimitConfig.getQrPayment().getWindowSeconds());
            }

            filterChain.doFilter(request, response);

        } catch (RateLimitExceededException ex) {

            filterExceptionHandler.handle(
                    response,
                    HttpStatus.TOO_MANY_REQUESTS,
                    ex.getMessage());

        } catch (UnauthorizedException ex) {

            filterExceptionHandler.handle(
                    response,
                    HttpStatus.UNAUTHORIZED,
                    ex.getMessage());
        }
    }

    private User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof User user)) {

            throw new UnauthorizedException("Unauthorized.");
        }

        return user;
    }

    private void checkRateLimit(
            String key,
            int limit,
            long windowSeconds) {

        boolean allowed = rateLimitService.isAllowed(
                key,
                limit,
                windowSeconds);

        if (!allowed) {
            throw new RateLimitExceededException(
                    "Too many requests. Please try again later.");
        }
    }
}