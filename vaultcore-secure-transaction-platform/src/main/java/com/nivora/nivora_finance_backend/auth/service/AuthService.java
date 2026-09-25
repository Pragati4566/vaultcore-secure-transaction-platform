package com.nivora.nivora_finance_backend.auth.service;

import java.util.List;

import com.nivora.nivora_finance_backend.auth.dto.request.LoginRequest;
import com.nivora.nivora_finance_backend.auth.dto.request.OtpVerifyRequest;
import com.nivora.nivora_finance_backend.auth.dto.request.SignupRequest;
import com.nivora.nivora_finance_backend.auth.dto.response.AuthResponse;
import com.nivora.nivora_finance_backend.auth.dto.response.UserProfileResponse;
import com.nivora.nivora_finance_backend.auth.dto.response.UserSearchResponse;

public interface AuthService {

    void signup(SignupRequest req);

    AuthResponse verifyOtp(OtpVerifyRequest req);

    AuthResponse login(LoginRequest req);

    void logout();

    UserProfileResponse getCurrentUser();

    List<UserSearchResponse> searchUsers(String keyword);

}