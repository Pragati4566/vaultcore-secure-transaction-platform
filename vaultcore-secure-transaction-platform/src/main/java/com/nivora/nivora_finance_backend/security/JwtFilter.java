package com.nivora.nivora_finance_backend.security;

import com.nivora.nivora_finance_backend.auth.entity.User;
import com.nivora.nivora_finance_backend.auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository repo;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            String email = jwtService.extractEmail(token);

            String storedToken =
                    redisTemplate.opsForValue()
                            .get("jwt:" + email);

            if (storedToken == null ||
                    !storedToken.equals(token)) {

                log.warn(
                        "Session expired or user logged out: {}",
                        email);

                filterChain.doFilter(request, response);
                return;
            }

            if (email != null &&
                    SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                User user = repo.findByEmail(email)
                        .orElse(null);

                if (user != null) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    Collections.emptyList());

                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);

                    log.info(
                            "User authenticated: {}",
                            email);
                }
            }

        } catch (Exception e) {

            log.error(
                    "JWT validation failed: {}",
                    e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}