package com.nivora.nivora_finance_backend.transaction.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.nivora.nivora_finance_backend.transaction.dto.request.TransferRequest;
import com.nivora.nivora_finance_backend.transaction.dto.response.RecentContactResponse;
import com.nivora.nivora_finance_backend.transaction.dto.response.TransactionResponse;
import com.nivora.nivora_finance_backend.transaction.dto.response.TransactionSummaryResponse;

public interface TransactionService {

        TransactionResponse transferMoney(
                        TransferRequest req,
                        String idempotencyKey);

        Page<TransactionResponse> getMyTransactions(
        int page,
        int size);

        TransactionResponse getTransactionById(
                        Long transactionId);

        List<TransactionResponse> searchTransactions(String keyword);

        TransactionSummaryResponse getSummary();

        List<RecentContactResponse> getRecentContacts();
}
