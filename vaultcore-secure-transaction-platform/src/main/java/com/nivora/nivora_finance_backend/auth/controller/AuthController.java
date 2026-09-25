package com.nivora.nivora_finance_backend.auth.controller;

import com.nivora.nivora_finance_backend.auth.dto.request.LoginRequest;
import com.nivora.nivora_finance_backend.auth.dto.request.OtpVerifyRequest;
import com.nivora.nivora_finance_backend.auth.dto.request.SignupRequest;
import com.nivora.nivora_finance_backend.auth.dto.response.AuthResponse;
import com.nivora.nivora_finance_backend.auth.dto.response.UserProfileResponse;
import com.nivora.nivora_finance_backend.auth.dto.response.UserSearchResponse;
import com.nivora.nivora_finance_backend.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "User registration, login, OTP verification and profile management")
@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

        private final AuthService authService;

        @Operation(summary = "Register new user", description = "Creates a new user account and sends OTP verification email.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Signup successful"),
                        @ApiResponse(responseCode = "400", description = "Invalid request"),
                        @ApiResponse(responseCode = "409", description = "Email already exists")
        })
        @PostMapping("/signup")
        public ResponseEntity<AuthResponse> signup(
                        @RequestBody SignupRequest req) {

                authService.signup(req);

                return ResponseEntity.ok(
                                AuthResponse.builder()
                                                .message("Signup successful")
                                                .token(null)
                                                .build());
        }

        @Operation(summary = "Verify OTP", description = "Verifies OTP, activates account and creates wallet.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid or expired OTP")
        })
        @PostMapping("/verify-otp")
        public ResponseEntity<AuthResponse> verifyOtp(
                        @RequestBody OtpVerifyRequest req) {

                AuthResponse res = authService.verifyOtp(req);

                return ResponseEntity.ok(res);
        }

        @Operation(summary = "User login", description = "Authenticates user and returns JWT token.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Login successful"),
                        @ApiResponse(responseCode = "401", description = "Invalid credentials")
        })
        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(
                        @RequestBody LoginRequest req) {

                AuthResponse res = authService.login(req);

                return ResponseEntity.ok(res);
        }

        @Operation(summary = "Logout", description = "Removes active JWT session from Redis.")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Logout successful"),
                        @ApiResponse(responseCode = "401", description = "User not authenticated")
        })
        @PostMapping("/logout")
        public ResponseEntity<String> logout() {

                authService.logout();

                return ResponseEntity.ok("Logout successful");
        }

        @Operation(summary = "Current user profile", description = "Returns authenticated user's profile information.")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "User not authenticated")
        })
        @GetMapping("/me")
        public ResponseEntity<UserProfileResponse> getCurrentUser() {

                UserProfileResponse res = authService.getCurrentUser();

                return ResponseEntity.ok(res);
        }

        @Operation(summary = "Search users", description = "Searches users by name or email for money transfers.")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "User not authenticated")
        })
        @GetMapping("/users/search")
        public ResponseEntity<List<UserSearchResponse>> searchUsers(
                        @RequestParam String keyword) {

                List<UserSearchResponse> users = authService.searchUsers(keyword);

                return ResponseEntity.ok(users);
        }
}