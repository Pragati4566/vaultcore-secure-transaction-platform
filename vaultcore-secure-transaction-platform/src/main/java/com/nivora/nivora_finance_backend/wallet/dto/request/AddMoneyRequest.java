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
@Schema(description = "Add money request")
public class AddMoneyRequest {

    @NotNull(message = "Amount is required")
@DecimalMin(value = "1.00", message = "Minimum amount is 1")
@DecimalMax(value = "100.00", message = "Maximum amount is 100")
@Schema(
        description = "Amount to credit into wallet",
        example = "100.00"
)
private BigDecimal amount;
}