package com.nivora.nivora_finance_backend.transaction.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Money transfer request")
public class TransferRequest {

        @NotNull(message = "Receiver id is required")
        @Schema(description = "Receiver user id", example = "2")
        private Long receiverId;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Minimum transfer amount is 1")
        @DecimalMax(value = "100.00", message = "Maximum transfer amount is 100")
        @Schema(description = "Amount to transfer", example = "25.00")
        private BigDecimal amount;
}