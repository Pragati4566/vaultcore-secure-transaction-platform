package com.nivora.nivora_finance_backend.transaction.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionSummaryResponse {

    private BigDecimal totalSent;

    private BigDecimal totalReceived;

    private Long transactionCount;
}
