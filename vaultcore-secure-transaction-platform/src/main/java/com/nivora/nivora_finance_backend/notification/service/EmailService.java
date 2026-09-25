package com.nivora.nivora_finance_backend.notification.service;

public interface EmailService {

    void sendOtpEmail(
            String email,
            String otp);

    void sendWelcomeEmail(
            String email,
            String name);
}