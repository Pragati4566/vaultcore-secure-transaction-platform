package com.nivora.nivora_finance_backend.wallet.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponse {

    private String message;
    private BigDecimal balance;
}