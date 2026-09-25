package com.nivora.nivora_finance_backend.auth.service;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.nivora.nivora_finance_backend.auth.dto.request.LoginRequest;
import com.nivora.nivora_finance_backend.auth.dto.request.OtpVerifyRequest;
import com.nivora.nivora_finance_backend.auth.dto.request.SignupRequest;
import com.nivora.nivora_finance_backend.auth.dto.response.AuthResponse;
import com.nivora.nivora_finance_backend.auth.dto.response.UserProfileResponse;
import com.nivora.nivora_finance_backend.auth.entity.User;
import com.nivora.nivora_finance_backend.common.exception.UserAlreadyExistsException;
import com.nivora.nivora_finance_backend.notification.service.EmailService;
import com.nivora.nivora_finance_backend.auth.repository.UserRepository;
import com.nivora.nivora_finance_backend.auth.service.impl.AuthServiceImpl;
import com.nivora.nivora_finance_backend.security.JwtService;
import com.nivora.nivora_finance_backend.wallet.entity.Wallet;
import com.nivora.nivora_finance_backend.wallet.repository.WalletRepository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import com.nivora.nivora_finance_backend.common.exception.InvalidCredentialsException;
import com.nivora.nivora_finance_backend.common.exception.InvalidOtpException;
import com.nivora.nivora_finance_backend.common.exception.UnauthorizedException;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

        @Mock
        private ValueOperations<String, String> valueOperations;

        @Mock
        private UserRepository repo;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private RedisTemplate<String, String> redisTemplate;

        @Mock
        private JwtService jwtService;

        @InjectMocks
        private AuthServiceImpl authService;

        @Mock
        private WalletRepository walletRepository;

        @Mock
        private EmailService emailService;

        @Test
        void signup_ShouldCreateUser_WhenEmailDoesNotExist() {

                SignupRequest req = SignupRequest.builder()
                                .name("Test User")
                                .email("test@example.com")
                                .password("testPassword123")
                                .build();

                when(repo.existsByEmail(req.getEmail()))
                                .thenReturn(false);

                when(passwordEncoder.encode(req.getPassword()))
                                .thenReturn("encodedPassword");

                when(redisTemplate.opsForValue())
                                .thenReturn(valueOperations);

                authService.signup(req);

                verify(repo, times(1))
                                .save(any(User.class));
        }

        @Test
        void signup_ShouldThrowException_WhenEmailExists() {

                SignupRequest req = SignupRequest.builder()
                                .name("Test User")
                                .email("test@example.com")
                                .password("testPassword123")
                                .build();

                when(repo.existsByEmail(req.getEmail()))
                                .thenReturn(true);

                assertThrows(
                                UserAlreadyExistsException.class,
                                () -> authService.signup(req));

                verify(repo, never())
                                .save(any(User.class));
        }

        @Test
        void login_ShouldReturnToken_WhenCredentialsAreValid() {

                LoginRequest req = LoginRequest.builder()
                                .email("test@example.com")
                                .password("password123")
                                .build();

                User user = User.builder()
                                .email("test@example.com")
                                .password("encodedPassword")
                                .verified(true)
                                .build();

                when(repo.findByEmail(req.getEmail()))
                                .thenReturn(Optional.of(user));

                when(passwordEncoder.matches(
                                req.getPassword(),
                                user.getPassword()))
                                .thenReturn(true);

                when(jwtService.generateToken(user.getEmail()))
                                .thenReturn("jwt-token");

                when(redisTemplate.opsForValue())
                                .thenReturn(valueOperations);

                AuthResponse response = authService.login(req);

                assertEquals(
                                "Login successful",
                                response.getMessage());

                assertEquals(
                                "jwt-token",
                                response.getToken());
        }

        @Test
        void login_ShouldThrowException_WhenPasswordIsWrong() {

                LoginRequest req = LoginRequest.builder()
                                .email("test@example.com")
                                .password("wrongPassword")
                                .build();

                User user = User.builder()
                                .email("test@example.com")
                                .password("encodedPassword")
                                .verified(true)
                                .build();

                when(repo.findByEmail(req.getEmail()))
                                .thenReturn(Optional.of(user));

                when(passwordEncoder.matches(
                                req.getPassword(),
                                user.getPassword()))
                                .thenReturn(false);

                assertThrows(
                                InvalidCredentialsException.class,
                                () -> authService.login(req));
        }

        @Test
        void login_ShouldThrowException_WhenUserNotFound() {

                LoginRequest req = LoginRequest.builder()
                                .email("test@example.com")
                                .password("password123")
                                .build();

                when(repo.findByEmail(req.getEmail()))
                                .thenReturn(Optional.empty());

                assertThrows(
                                InvalidCredentialsException.class,
                                () -> authService.login(req));
        }

        @Test
        void login_ShouldThrowException_WhenUserIsNotVerified() {

                LoginRequest req = LoginRequest.builder()
                                .email("test@example.com")
                                .password("password123")
                                .build();

                User user = User.builder()
                                .email("test@example.com")
                                .password("encodedPassword")
                                .verified(false)
                                .build();

                when(repo.findByEmail(req.getEmail()))
                                .thenReturn(Optional.of(user));

                assertThrows(
                                UnauthorizedException.class,
                                () -> authService.login(req));
        }

        @Test
        void verifyOtp_ShouldVerifyUser_WhenOtpIsValid() {

                OtpVerifyRequest req = OtpVerifyRequest.builder()
                                .email("test@example.com")
                                .otp("123456")
                                .build();

                User user = User.builder()
                                .email("test@example.com")
                                .verified(false)
                                .build();

                when(redisTemplate.opsForValue())
                                .thenReturn(valueOperations);

                when(valueOperations.get("otp:test@example.com"))
                                .thenReturn("123456");

                when(repo.findByEmail(req.getEmail()))
                                .thenReturn(Optional.of(user));

                AuthResponse response = authService.verifyOtp(req);

                assertEquals(
                                "OTP verified successfully",
                                response.getMessage());

                verify(repo).save(user);
                verify(redisTemplate)
                                .delete("otp:test@example.com");
                verify(walletRepository)
                                .save(any(Wallet.class));
        }

        @Test
        void verifyOtp_ShouldThrowException_WhenOtpIsInvalid() {

                OtpVerifyRequest req = OtpVerifyRequest.builder()
                                .email("test@example.com")
                                .otp("999999")
                                .build();

                when(redisTemplate.opsForValue())
                                .thenReturn(valueOperations);

                when(valueOperations.get("otp:test@example.com"))
                                .thenReturn("123456");

                assertThrows(
                                InvalidOtpException.class,
                                () -> authService.verifyOtp(req));
        }

        @Test
        void verifyOtp_ShouldThrowException_WhenOtpExpired() {

                OtpVerifyRequest req = OtpVerifyRequest.builder()
                                .email("test@example.com")
                                .otp("123456")
                                .build();

                when(redisTemplate.opsForValue())
                                .thenReturn(valueOperations);

                when(valueOperations.get("otp:test@example.com"))
                                .thenReturn(null);

                assertThrows(
                                InvalidOtpException.class,
                                () -> authService.verifyOtp(req));
        }

        @Test
        void logout_ShouldDeleteJwtFromRedis() {

                User user = User.builder()
                                .email("test@example.com")
                                .build();

                Authentication authentication = mock(Authentication.class);

                when(authentication.getPrincipal())
                                .thenReturn(user);

                SecurityContextHolder.getContext()
                                .setAuthentication(authentication);

                authService.logout();

                verify(redisTemplate)
                                .delete("jwt:test@example.com");
        }

        @Test
        void getCurrentUser_ShouldReturnProfile() {

                User user = User.builder()
                                .id(1L)
                                .name("Test User")
                                .email("test@example.com")
                                .build();

                Authentication authentication = mock(Authentication.class);

                when(authentication.getPrincipal())
                                .thenReturn(user);

                SecurityContextHolder.getContext()
                                .setAuthentication(authentication);

                UserProfileResponse response = authService.getCurrentUser();

                assertEquals(1L, response.getId());
                assertEquals("Test User", response.getName());
                assertEquals("test@example.com", response.getEmail());
        }

}
