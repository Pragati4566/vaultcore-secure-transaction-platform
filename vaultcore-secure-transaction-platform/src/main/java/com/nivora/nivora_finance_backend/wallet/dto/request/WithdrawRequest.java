package com.nivora.nivora_finance_backend.wallet.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Withdraw money request")
public class WithdrawRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Minimum withdrawal amount is 1")
    @DecimalMax(value = "100.00", message = "Maximum withdrawal amount is 100")
    @Schema(description = "Amount to withdraw from wallet", example = "50.00")
    private BigDecimal amount;
}