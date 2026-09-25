package com.nivora.nivora_finance_backend.transaction.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import com.nivora.nivora_finance_backend.transaction.mapper.TransactionMapper;
import com.nivora.nivora_finance_backend.transaction.projection.TransactionSummaryProjection;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.nivora.nivora_finance_backend.auth.entity.User;
import com.nivora.nivora_finance_backend.auth.repository.UserRepository;
import com.nivora.nivora_finance_backend.common.exception.InsufficientFundsException;
import com.nivora.nivora_finance_backend.common.exception.ResourceNotFoundException;
import com.nivora.nivora_finance_backend.transaction.dto.request.TransferRequest;
import com.nivora.nivora_finance_backend.transaction.dto.response.TransactionResponse;
import com.nivora.nivora_finance_backend.transaction.dto.response.TransactionSummaryResponse;
import com.nivora.nivora_finance_backend.transaction.entity.Transaction;
import com.nivora.nivora_finance_backend.transaction.entity.TransactionDirection;
import com.nivora.nivora_finance_backend.transaction.entity.TransactionStatus;
import com.nivora.nivora_finance_backend.transaction.entity.TransactionType;
import com.nivora.nivora_finance_backend.transaction.repository.TransactionRepository;
import com.nivora.nivora_finance_backend.transaction.service.impl.TransactionServiceImpl;
import com.nivora.nivora_finance_backend.wallet.entity.Wallet;
import com.nivora.nivora_finance_backend.wallet.repository.WalletRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

        @Mock
        private TransactionRepository transactionRepository;

        @Mock
        private WalletRepository walletRepository;

        @Mock
        private UserRepository userRepository;

        @Mock
        private SecurityContext securityContext;

        @Mock
        private TransactionMapper transactionMapper;

        @InjectMocks
        private TransactionServiceImpl transactionService;

        private User sender;
        private User receiver;
        private Wallet senderWallet;
        private Wallet receiverWallet;

        @BeforeEach
        void setUp() {
                sender = new User();
                sender.setId(1L);

                receiver = new User();
                receiver.setId(2L);

                senderWallet = Wallet.builder()
                                .balance(BigDecimal.valueOf(100))
                                .build();

                receiverWallet = Wallet.builder()
                                .balance(BigDecimal.valueOf(50))
                                .build();

                SecurityContextHolder.setContext(securityContext);
                when(securityContext.getAuthentication())
                                .thenReturn(mock(org.springframework.security.core.Authentication.class));

                org.springframework.security.core.Authentication authentication = securityContext.getAuthentication();

                when(authentication.getPrincipal()).thenReturn(sender);

                lenient().when(transactionMapper.toResponse(
                                any(Transaction.class),
                                anyLong()))
                                .thenAnswer(invocation -> {

                                        Transaction t = invocation.getArgument(0);
                                        Long userId = invocation.getArgument(1);

                                        TransactionDirection direction = t.getSenderId().equals(userId)
                                                        ? TransactionDirection.DEBIT
                                                        : TransactionDirection.CREDIT;

                                        return TransactionResponse.builder()
                                                        .transactionId(t.getId())
                                                        .senderId(t.getSenderId())
                                                        .receiverId(t.getReceiverId())
                                                        .amount(t.getAmount())
                                                        .status(t.getStatus())
                                                        .type(t.getType())
                                                        .direction(direction)
                                                        .build();
                                });
        }

        @Test
        void transferMoney_ShouldTransferSuccessfully() {

                TransferRequest request = new TransferRequest();
                request.setReceiverId(2L);
                request.setAmount(BigDecimal.valueOf(20));

                when(userRepository.findById(2L))
                                .thenReturn(Optional.of(receiver));

                when(walletRepository.findByUserIdWithLock(1L))
                                .thenReturn(Optional.of(senderWallet));

                when(walletRepository.findByUserIdWithLock(2L))
                                .thenReturn(Optional.of(receiverWallet));

                when(transactionRepository.save(any(Transaction.class)))
                                .thenAnswer(invocation -> {
                                        Transaction t = invocation.getArgument(0);
                                        t.setId(1L);
                                        return t;
                                });

                TransactionResponse response = transactionService.transferMoney(
                                request,
                                "test-key");

                assertNotNull(response);
                assertEquals(1L, response.getTransactionId());
                assertEquals("Transfer successful", response.getMessage());

                assertEquals(
                                BigDecimal.valueOf(80),
                                senderWallet.getBalance());

                assertEquals(
                                BigDecimal.valueOf(70),
                                receiverWallet.getBalance());
        }

        @Test
        void transferMoney_ShouldThrow_WhenInsufficientFunds() {

                senderWallet.setBalance(BigDecimal.valueOf(5));

                TransferRequest request = new TransferRequest();
                request.setReceiverId(2L);
                request.setAmount(BigDecimal.valueOf(20));

                when(userRepository.findById(2L))
                                .thenReturn(Optional.of(receiver));

                when(walletRepository.findByUserIdWithLock(1L))
                                .thenReturn(Optional.of(senderWallet));

                when(walletRepository.findByUserIdWithLock(2L))
                                .thenReturn(Optional.of(receiverWallet));

                assertThrows(
                                InsufficientFundsException.class,
                                () -> transactionService.transferMoney(
                                                request,
                                                "test-key"));
        }

        @Test
        void getMyTransactions_ShouldReturnTransactions() {

                Transaction transaction = Transaction.builder()
                                .id(1L)
                                .senderId(1L)
                                .receiverId(2L)
                                .amount(BigDecimal.TEN)
                                .status(TransactionStatus.SUCCESS)
                                .type(TransactionType.TRANSFER)
                                .build();

                Page<Transaction> page = new PageImpl<>(
                                List.of(transaction),
                                PageRequest.of(0, 20),
                                1);

                when(transactionRepository.findBySenderIdOrReceiverId(
                                eq(1L),
                                eq(1L),
                                any()))
                                .thenReturn(page);

                Page<TransactionResponse> responses = transactionService.getMyTransactions(
                                0,
                                20);

                assertEquals(
                                1,
                                responses.getContent().size());

                assertEquals(
                                TransactionDirection.DEBIT,
                                responses.getContent()
                                                .get(0)
                                                .getDirection());
        }

        @Test
        void getTransactionById_ShouldReturnTransaction() {

                Transaction transaction = Transaction.builder()
                                .id(1L)
                                .senderId(1L)
                                .receiverId(2L)
                                .amount(BigDecimal.TEN)
                                .status(TransactionStatus.SUCCESS)
                                .type(TransactionType.TRANSFER)
                                .build();

                when(transactionRepository.findById(1L))
                                .thenReturn(Optional.of(transaction));

                TransactionResponse response = transactionService.getTransactionById(1L);

                assertEquals(1L, response.getTransactionId());
                assertEquals(
                                TransactionDirection.DEBIT,
                                response.getDirection());
        }

        @Test
        void getTransactionById_ShouldThrow_WhenTransactionNotFound() {

                when(transactionRepository.findById(1L))
                                .thenReturn(Optional.empty());

                assertThrows(
                                ResourceNotFoundException.class,
                                () -> transactionService.getTransactionById(1L));
        }

        @Test
        void transferMoney_ShouldThrow_WhenAmountLessThan1() {

                TransferRequest request = new TransferRequest();
                request.setReceiverId(2L);
                request.setAmount(BigDecimal.ZERO);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> transactionService.transferMoney(
                                                request,
                                                "test-key"));
        }

        @Test
        void transferMoney_ShouldThrow_WhenAmountGreaterThan100() {

                TransferRequest request = new TransferRequest();
                request.setReceiverId(2L);
                request.setAmount(BigDecimal.valueOf(101));

                assertThrows(
                                IllegalArgumentException.class,
                                () -> transactionService.transferMoney(
                                                request,
                                                "test-key"));
        }

        @Test
        void transferMoney_ShouldThrow_WhenReceiverNotFound() {

                TransferRequest request = new TransferRequest();
                request.setReceiverId(2L);
                request.setAmount(BigDecimal.TEN);

                when(userRepository.findById(2L))
                                .thenReturn(Optional.empty());

                assertThrows(
                                ResourceNotFoundException.class,
                                () -> transactionService.transferMoney(
                                                request,
                                                "test-key"));
        }

        @Test
        void transferMoney_ShouldThrow_WhenSelfTransfer() {

                TransferRequest request = new TransferRequest();
                request.setReceiverId(1L);
                request.setAmount(BigDecimal.TEN);

                when(userRepository.findById(1L))
                                .thenReturn(Optional.of(sender));

                assertThrows(
                                IllegalArgumentException.class,
                                () -> transactionService.transferMoney(
                                                request,
                                                "test-key"));
        }

        @Test
        void getTransactionById_ShouldThrow_WhenUserDoesNotOwnTransaction() {

                Transaction transaction = Transaction.builder()
                                .id(1L)
                                .senderId(2L)
                                .receiverId(3L)
                                .amount(BigDecimal.TEN)
                                .status(TransactionStatus.SUCCESS)
                                .type(TransactionType.TRANSFER)
                                .build();

                when(transactionRepository.findById(1L))
                                .thenReturn(Optional.of(transaction));

                assertThrows(
                                ResourceNotFoundException.class,
                                () -> transactionService.getTransactionById(1L));
        }

        @Test
        void getMyTransactions_ShouldReturnCreditDirection() {

                Transaction transaction = Transaction.builder()
                                .id(1L)
                                .senderId(2L)
                                .receiverId(1L)
                                .amount(BigDecimal.TEN)
                                .status(TransactionStatus.SUCCESS)
                                .type(TransactionType.TRANSFER)
                                .build();

                Page<Transaction> page = new PageImpl<>(
                                List.of(transaction),
                                PageRequest.of(0, 20),
                                1);

                when(transactionRepository.findBySenderIdOrReceiverId(
                                eq(1L),
                                eq(1L),
                                any()))
                                .thenReturn(page);

                Page<TransactionResponse> responses = transactionService.getMyTransactions(
                                0,
                                20);

                assertEquals(
                                1,
                                responses.getContent().size());

                assertEquals(
                                TransactionDirection.CREDIT,
                                responses.getContent()
                                                .get(0)
                                                .getDirection());
        }

        @Test
        void searchTransactions_ShouldReturnMatchingTransactions() {

                Transaction transaction = Transaction.builder()
                                .id(1L)
                                .senderId(1L)
                                .receiverId(2L)
                                .amount(BigDecimal.TEN)
                                .status(TransactionStatus.SUCCESS)
                                .type(TransactionType.TRANSFER)
                                .build();

                when(transactionRepository.searchTransactions(
                                1L,
                                "success"))
                                .thenReturn(List.of(transaction));

                List<TransactionResponse> responses = transactionService.searchTransactions(
                                "success");

                assertEquals(
                                1,
                                responses.size());

                assertEquals(
                                TransactionDirection.DEBIT,
                                responses.get(0).getDirection());

                verify(transactionRepository)
                                .searchTransactions(
                                                1L,
                                                "success");
        }

        @Test
        void getSummary_ShouldReturnSummary() {
                // Arrange
                TransactionSummaryProjection projection = mock(TransactionSummaryProjection.class);

                when(projection.getTotalSent()).thenReturn(new BigDecimal("120.00"));
                when(projection.getTotalReceived()).thenReturn(new BigDecimal("80.00"));
                when(projection.getTransactionCount()).thenReturn(5L);

                when(transactionRepository.getTransactionSummary(1L))
                                .thenReturn(projection);

                // Act
                TransactionSummaryResponse response = transactionService.getSummary();

                // Assert
                assertEquals(new BigDecimal("120.00"), response.getTotalSent());
                assertEquals(new BigDecimal("80.00"), response.getTotalReceived());
                assertEquals(5L, response.getTransactionCount());

                verify(transactionRepository).getTransactionSummary(1L);
        }

        @AfterEach
        void tearDown() {
                SecurityContextHolder.clearContext();
        }

}