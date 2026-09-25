package com.nivora.nivora_finance_backend.transaction.projection;

import java.math.BigDecimal;

public interface TransactionSummaryProjection {

    BigDecimal getTotalSent();

    BigDecimal getTotalReceived();

    Long getTransactionCount();
}