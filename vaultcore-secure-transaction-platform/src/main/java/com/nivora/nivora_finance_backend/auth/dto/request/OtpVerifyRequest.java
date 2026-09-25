package com.nivora.nivora_finance_backend.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "OTP verification request")
public class OtpVerifyRequest {

    @Schema(
            description = "Registered email address",
            example = "harsh@gmail.com"
    )
    @NotBlank
    @Email
    private String email;

    @Schema(
            description = "6 digit OTP sent to email",
            example = "123456"
    )
    @NotBlank
    @Size(min = 6, max = 6)
    private String otp;
}