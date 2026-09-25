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
@Schema(description = "User registration request")
public class SignupRequest {

    @Schema(
            description = "Full name of the user",
            example = "Harsh Jha"
    )
    @NotBlank
    private String name;

    @Schema(
            description = "User email address",
            example = "harsh@gmail.com"
    )
    @NotBlank
    @Email
    private String email;

    @Schema(
            description = "Password (minimum 8 characters)",
            example = "Password@123"
    )
    @NotBlank
    @Size(min = 8)
    private String password;
}