package com.nivora.nivora_finance_backend.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "User login request")
public class LoginRequest {

    @Schema(
            description = "Registered email address",
            example = "harsh@gmail.com"
    )
    @NotBlank
    @Email
    private String email;

    @Schema(
            description = "Account password",
            example = "Password@123"
    )
    @NotBlank
    private String password;
}