package com.nivora.nivora_finance_backend.qr.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QrPaymentRequest {

    @NotBlank
    private String qrData;

    @NotNull
    private BigDecimal amount;
}