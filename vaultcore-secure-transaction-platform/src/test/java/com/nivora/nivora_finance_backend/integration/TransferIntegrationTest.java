package com.nivora.nivora_finance_backend.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nivora.nivora_finance_backend.auth.entity.User;
import com.nivora.nivora_finance_backend.auth.repository.UserRepository;
import com.nivora.nivora_finance_backend.transaction.dto.request.TransferRequest;
import com.nivora.nivora_finance_backend.transaction.dto.response.TransactionResponse;
import com.nivora.nivora_finance_backend.transaction.entity.TransactionStatus;
import com.nivora.nivora_finance_backend.transaction.repository.TransactionRepository;
import com.nivora.nivora_finance_backend.transaction.service.TransactionService;
import com.nivora.nivora_finance_backend.wallet.entity.Wallet;
import com.nivora.nivora_finance_backend.wallet.repository.WalletRepository;

class TransferIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldTransferMoneySuccessfully() {

        // Arrange 

        User sender = createUser("Sender");
        User receiver = createUser("Receiver");

        createWallet(sender, "100.00");
        createWallet(receiver, "20.00");

        authenticate(sender);

        TransferRequest request = TransferRequest.builder()
                .receiverId(receiver.getId())
                .amount(new BigDecimal("50.00"))
                .build();

        String idempotencyKey = UUID.randomUUID().toString();

        // Act 
        

        TransactionResponse response =
                transactionService.transferMoney(request, idempotencyKey);

        // Assert

        Wallet updatedSenderWallet = walletRepository
                .findByUserId(sender.getId())
                .orElseThrow();

        Wallet updatedReceiverWallet = walletRepository
                .findByUserId(receiver.getId())
                .orElseThrow();

        assertEquals(
                new BigDecimal("50.00"),
                updatedSenderWallet.getBalance());

        assertEquals(
                new BigDecimal("70.00"),
                updatedReceiverWallet.getBalance());

        assertEquals(
                1,
                transactionRepository.count());

        assertEquals(
                TransactionStatus.SUCCESS,
                response.getStatus());

        assertEquals(
                new BigDecimal("50.00"),
                response.getAmount());
    }

    private User createUser(String name) {

        String unique = UUID.randomUUID().toString();

        return userRepository.save(
                User.builder()
                        .name(name)
                        .email(unique + "@test.com")
                        .password("password")
                        .verified(true)
                        .build());
    }

    private Wallet createWallet(User user, String balance) {

        return walletRepository.save(
                Wallet.builder()
                        .user(user)
                        .balance(new BigDecimal(balance))
                        .build());
    }

    private void authenticate(User user) {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.emptyList());

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }
}