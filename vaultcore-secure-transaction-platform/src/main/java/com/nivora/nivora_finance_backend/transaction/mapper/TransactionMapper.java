package com.nivora.nivora_finance_backend.transaction.mapper;

import org.springframework.stereotype.Component;

import com.nivora.nivora_finance_backend.transaction.dto.response.TransactionResponse;
import com.nivora.nivora_finance_backend.transaction.entity.Transaction;
import com.nivora.nivora_finance_backend.transaction.entity.TransactionDirection;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(
            Transaction transaction,
            Long currentUserId) {

        TransactionDirection direction =
                transaction.getSenderId().equals(currentUserId)
                        ? TransactionDirection.DEBIT
                        : TransactionDirection.CREDIT;

        return TransactionResponse.builder()
                .transactionId(transaction.getId())
                .senderId(transaction.getSenderId())
                .receiverId(transaction.getReceiverId())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .type(transaction.getType())
                .direction(direction)
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
