package com.nivora.nivora_finance_backend.auth.service.impl;

import com.nivora.nivora_finance_backend.auth.dto.request.LoginRequest;
import com.nivora.nivora_finance_backend.auth.dto.request.OtpVerifyRequest;
import com.nivora.nivora_finance_backend.auth.dto.request.SignupRequest;
import com.nivora.nivora_finance_backend.auth.dto.response.AuthResponse;
import com.nivora.nivora_finance_backend.auth.dto.response.UserProfileResponse;
import com.nivora.nivora_finance_backend.auth.dto.response.UserSearchResponse;
import com.nivora.nivora_finance_backend.auth.entity.User;
import com.nivora.nivora_finance_backend.auth.repository.UserRepository;
import com.nivora.nivora_finance_backend.auth.service.AuthService;
import com.nivora.nivora_finance_backend.common.exception.InvalidCredentialsException;
import com.nivora.nivora_finance_backend.common.exception.InvalidOtpException;
import com.nivora.nivora_finance_backend.common.exception.ResourceNotFoundException;
import com.nivora.nivora_finance_backend.common.exception.UnauthorizedException;
import com.nivora.nivora_finance_backend.common.exception.UserAlreadyExistsException;
import com.nivora.nivora_finance_backend.notification.service.EmailService;
import com.nivora.nivora_finance_backend.security.JwtService;
import com.nivora.nivora_finance_backend.wallet.entity.Wallet;
import com.nivora.nivora_finance_backend.wallet.repository.WalletRepository;

import org.springframework.security.core.Authentication;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

        private final UserRepository repo;
        private final PasswordEncoder passwordEncoder;
        private final RedisTemplate<String, String> redisTemplate;
        private final JwtService jwtService;
        private final WalletRepository walletRepository;
        private final EmailService emailService;

        @Override
        public void signup(SignupRequest req) {

                String reqEmail = req.getEmail();
                String reqName = req.getName();
                String reqPassword = req.getPassword();

                boolean emailExists = repo.existsByEmail(reqEmail);
                if (emailExists) {
                        throw new UserAlreadyExistsException(
                                        "Email already exists");
                }

                String encodedPassword = passwordEncoder.encode(reqPassword);

                User newUser = User.builder()
                                .name(reqName)
                                .password(encodedPassword)
                                .email(reqEmail)
                                .verified(false)
                                .build();

                repo.save(newUser);

                String otp = generateOtp();

                redisTemplate.opsForValue().set(
                                "otp:" + reqEmail,
                                otp,
                                Duration.ofMinutes(5));

                emailService.sendOtpEmail(
                                reqEmail,
                                otp);

        }

        @Override
        public AuthResponse verifyOtp(OtpVerifyRequest req) {

                String storedOtp = redisTemplate.opsForValue()
                                .get("otp:" + req.getEmail());

                if (storedOtp == null) {
                        throw new InvalidOtpException(
                                        "OTP expired");
                }

                if (!storedOtp.equals(req.getOtp())) {
                        throw new InvalidOtpException(
                                        "Invalid OTP");
                }
                User user = repo.findByEmail(req.getEmail())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found"));

                user.setVerified(true);

                repo.save(user);

                Wallet wallet = Wallet.builder()
                                .user(user)
                                .balance(BigDecimal.ZERO)
                                .build();

                walletRepository.save(wallet);

                emailService.sendWelcomeEmail(
                                user.getEmail(),
                                user.getName());

                redisTemplate.delete(
                                "otp:" + req.getEmail());

                log.info(
                                "User verified successfully: {}",
                                req.getEmail());

                String token = jwtService.generateToken(
                                user.getEmail());

                redisTemplate.opsForValue().set(
                                "jwt:" + user.getEmail(),
                                token,
                                Duration.ofHours(3));

                return AuthResponse.builder()
                                .message("OTP verified successfully")
                                .token(token)
                                .build();

        }

        @Override
        public AuthResponse login(LoginRequest req) {

                User user = repo.findByEmail(req.getEmail())
                                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

                if (!user.getVerified()) {
                        throw new UnauthorizedException(
                                        "Please verify your account first");
                }

                boolean passwordMatches = passwordEncoder.matches(
                                req.getPassword(),
                                user.getPassword());

                if (!passwordMatches) {

                        log.warn(
                                        "Invalid login attempt for email: {}",
                                        req.getEmail());

                        throw new InvalidCredentialsException(
                                        "Invalid credentials");
                }

                String token = jwtService.generateToken(
                                user.getEmail());

                redisTemplate.opsForValue().set(
                                "jwt:" + user.getEmail(),
                                token,
                                Duration.ofHours(3));

                log.info(
                                "Login successful for email: {}",
                                req.getEmail());

                return AuthResponse.builder()
                                .message("Login successful")
                                .token(token)
                                .build();
        }

        @Override
        public void logout() {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null ||
                                !(authentication.getPrincipal() instanceof User user)) {

                        throw new UnauthorizedException(
                                        "User not authenticated");
                }

                redisTemplate.delete(
                                "jwt:" + user.getEmail());

                SecurityContextHolder.clearContext();

                log.info(
                                "User logged out: {}",
                                user.getEmail());
        }

        @Override
        public UserProfileResponse getCurrentUser() {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                User user = (User) authentication.getPrincipal();

                return UserProfileResponse.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .build();
        }

        @Override
        public List<UserSearchResponse> searchUsers(String keyword) {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                User currentUser = (User) authentication.getPrincipal();

                return repo
                                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                                                keyword,
                                                keyword)
                                .stream()
                                .filter(user -> !user.getId().equals(currentUser.getId()))
                                .map(user -> UserSearchResponse.builder()
                                                .id(user.getId())
                                                .name(user.getName())
                                                .email(user.getEmail())
                                                .build())
                                .toList();
        }

        private String generateOtp() {
                return String.valueOf(
                                ThreadLocalRandom.current().nextInt(100000, 1000000));
        }

}
