package com.nivora.nivora_finance_backend.transaction.service.impl;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nivora.nivora_finance_backend.auth.entity.User;
import com.nivora.nivora_finance_backend.auth.repository.UserRepository;
import com.nivora.nivora_finance_backend.common.exception.InsufficientFundsException;
import com.nivora.nivora_finance_backend.common.exception.ResourceNotFoundException;
import com.nivora.nivora_finance_backend.transaction.dto.request.TransferRequest;
import com.nivora.nivora_finance_backend.transaction.dto.response.RecentContactResponse;
import com.nivora.nivora_finance_backend.transaction.dto.response.TransactionResponse;
import com.nivora.nivora_finance_backend.transaction.dto.response.TransactionSummaryResponse;
import com.nivora.nivora_finance_backend.transaction.entity.Transaction;
import com.nivora.nivora_finance_backend.transaction.entity.TransactionStatus;
import com.nivora.nivora_finance_backend.transaction.entity.TransactionType;
import com.nivora.nivora_finance_backend.transaction.mapper.TransactionMapper;
import com.nivora.nivora_finance_backend.transaction.projection.TransactionSummaryProjection;
import com.nivora.nivora_finance_backend.transaction.repository.TransactionRepository;
import com.nivora.nivora_finance_backend.transaction.service.TransactionService;
import com.nivora.nivora_finance_backend.wallet.entity.Wallet;
import com.nivora.nivora_finance_backend.wallet.repository.WalletRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

        private final TransactionRepository transactionRepository;
        private final WalletRepository walletRepository;
        private final UserRepository userRepository;
        private final TransactionMapper transactionMapper;

        @Override
        @Transactional
        public TransactionResponse transferMoney(TransferRequest req, String idempotencyKey) {

                // Idempotency check — must be first
                Optional<Transaction> existing = transactionRepository.findByIdempotencyKey(idempotencyKey);
                if (existing.isPresent()) {
                        Transaction t = existing.get();
                        return TransactionResponse.builder()
                                        .transactionId(t.getId())
                                        .senderId(t.getSenderId())
                                        .receiverId(t.getReceiverId())
                                        .amount(t.getAmount())
                                        .status(t.getStatus())
                                        .type(t.getType())
                                        .createdAt(t.getCreatedAt())
                                        .message("Transfer successful")
                                        .build();
                }

                User sender = getCurrentUser();

                if (req.getAmount().compareTo(BigDecimal.ONE) < 0) {
                        throw new IllegalArgumentException("Minimum transfer amount is $1");
                }

                if (req.getAmount().compareTo(BigDecimal.valueOf(100)) > 0) {
                        throw new IllegalArgumentException("Maximum transfer amount is $100");
                }

                User receiver = userRepository.findById(req.getReceiverId())
                                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

                if (sender.getId().equals(receiver.getId())) {
                        throw new IllegalArgumentException("Cannot transfer money to yourself");
                }

                Wallet senderWallet = walletRepository
                                .findByUserIdWithLock(sender.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Sender wallet not found"));

                Wallet receiverWallet = walletRepository
                                .findByUserIdWithLock(receiver.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Receiver wallet not found"));

                if (senderWallet.getBalance().compareTo(req.getAmount()) < 0) {
                        throw new InsufficientFundsException("Insufficient wallet balance");
                }

                Transaction transaction = Transaction.builder()
                                .senderId(sender.getId())
                                .receiverId(receiver.getId())
                                .amount(req.getAmount())
                                .status(TransactionStatus.PENDING)
                                .type(TransactionType.TRANSFER)
                                .idempotencyKey(idempotencyKey)
                                .build();

                // Save transaction as PENDING first
                transaction = transactionRepository.save(transaction);

                // Debit sender wallet
                senderWallet.setBalance(
                                senderWallet.getBalance()
                                                .subtract(req.getAmount()));

                // Credit receiver wallet
                receiverWallet.setBalance(
                                receiverWallet.getBalance()
                                                .add(req.getAmount()));

                walletRepository.save(senderWallet);
                walletRepository.save(receiverWallet);

                // Mark transaction as SUCCESS
                transaction.setStatus(
                                TransactionStatus.SUCCESS);

                transaction = transactionRepository.save(transaction);

                return TransactionResponse.builder()
                                .transactionId(transaction.getId())
                                .senderId(transaction.getSenderId())
                                .receiverId(transaction.getReceiverId())
                                .amount(transaction.getAmount())
                                .status(transaction.getStatus())
                                .type(transaction.getType())
                                .createdAt(transaction.getCreatedAt())
                                .message("Transfer successful")
                                .build();
        }

        @Override
        public Page<TransactionResponse> getMyTransactions(
                        int page,
                        int size) {

                User user = getCurrentUser();

                Pageable pageable = PageRequest.of(page, size);

                Page<Transaction> transactions = transactionRepository
                                .findBySenderIdOrReceiverId(
                                                user.getId(),
                                                user.getId(),
                                                pageable);

                List<TransactionResponse> responses = transactions.getContent()
                                .stream()
                                .map(transaction -> transactionMapper.toResponse(
                                                transaction,
                                                user.getId()))
                                .toList();

                return new PageImpl<>(
                                responses,
                                pageable,
                                transactions.getTotalElements());
        }

        @Override
        public TransactionResponse getTransactionById(Long transactionId) {

                User user = getCurrentUser();

                Transaction transaction = transactionRepository.findById(transactionId)
                                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

                if (!transaction.getSenderId().equals(user.getId())
                                && !transaction.getReceiverId().equals(user.getId())) {
                        throw new ResourceNotFoundException("Transaction not found");
                }

                return transactionMapper.toResponse(
                                transaction,
                                user.getId());
        }

        private User getCurrentUser() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                return (User) authentication.getPrincipal();
        }

        @Override
        public List<TransactionResponse> searchTransactions(
                        String keyword) {

                User user = getCurrentUser();

                return transactionRepository
                                .searchTransactions(
                                                user.getId(),
                                                keyword)
                                .stream()
                                .map(transaction -> transactionMapper.toResponse(
                                                transaction,
                                                user.getId()))
                                .toList();
        }

        @Override
        public TransactionSummaryResponse getSummary() {

                User user = getCurrentUser();
                log.info("Summary requested by user: {}", user.getId());

                TransactionSummaryProjection summary = transactionRepository.getTransactionSummary(user.getId());

                return TransactionSummaryResponse.builder()
                                .totalSent(summary.getTotalSent())
                                .totalReceived(summary.getTotalReceived())
                                .transactionCount(summary.getTransactionCount())
                                .build();
        }

        @Override
        public List<RecentContactResponse> getRecentContacts() {

                User currentUser = getCurrentUser();

                List<Transaction> transactions = transactionRepository.findBySenderIdOrReceiverId(
                                currentUser.getId(),
                                currentUser.getId());

                Set<Long> contactIds = new LinkedHashSet<>();

                for (Transaction transaction : transactions) {

                        Long contactId;

                        if (transaction.getSenderId().equals(currentUser.getId())) {
                                contactId = transaction.getReceiverId();
                        } else {
                                contactId = transaction.getSenderId();
                        }

                        contactIds.add(contactId);
                }

                List<Long> contactIdList = List.copyOf(contactIds);

                List<User> contacts = userRepository.findAllById(contactIdList);

                return contacts.stream()
                                .map(user -> RecentContactResponse.builder()
                                                .userId(user.getId())
                                                .name(user.getName())
                                                .email(user.getEmail())
                                                .build())
                                .toList();
        }
}