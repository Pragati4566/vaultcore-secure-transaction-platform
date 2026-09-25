package com.nivora.nivora_finance_backend.transaction.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.nivora.nivora_finance_backend.transaction.entity.TransactionDirection;
import com.nivora.nivora_finance_backend.transaction.entity.TransactionStatus;
import com.nivora.nivora_finance_backend.transaction.entity.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {

    private Long transactionId;

    private Long senderId;

    private Long receiverId;

    private BigDecimal amount;

    private TransactionStatus status;

    private TransactionType type;

    private LocalDateTime createdAt;

    private TransactionDirection direction;

    private String message;
}
