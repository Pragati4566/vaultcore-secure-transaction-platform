package com.nivora.nivora_finance_backend.wallet.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nivora.nivora_finance_backend.auth.entity.User;
import com.nivora.nivora_finance_backend.common.exception.InsufficientFundsException;
import com.nivora.nivora_finance_backend.wallet.dto.request.AddMoneyRequest;
import com.nivora.nivora_finance_backend.wallet.dto.request.WithdrawRequest;
import com.nivora.nivora_finance_backend.wallet.dto.response.BalanceResponse;
import com.nivora.nivora_finance_backend.wallet.dto.response.WalletResponse;
import com.nivora.nivora_finance_backend.wallet.entity.Wallet;
import com.nivora.nivora_finance_backend.wallet.repository.WalletRepository;
import com.nivora.nivora_finance_backend.wallet.service.impl.WalletServiceImpl;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
void getBalance_ShouldReturnBalance() {

    User user = User.builder()
            .id(1L)
            .build();

    Wallet wallet = Wallet.builder()
            .user(user)
            .balance(BigDecimal.valueOf(50))
            .build();

    Authentication authentication =
            mock(Authentication.class);

    when(authentication.getPrincipal())
            .thenReturn(user);

    SecurityContextHolder.getContext()
            .setAuthentication(authentication);

   when(walletRepository.findByUserId(1L))
        .thenReturn(Optional.of(wallet));

    BalanceResponse response =
            walletService.getBalance();

    assertEquals(
            BigDecimal.valueOf(50),
            response.getBalance());
}

@Test
void addMoney_ShouldIncreaseBalance() {

    User user = User.builder()
            .id(1L)
            .build();

    Wallet wallet = Wallet.builder()
            .user(user)
            .balance(BigDecimal.valueOf(10))
            .build();

    AddMoneyRequest req =
            AddMoneyRequest.builder()
                    .amount(BigDecimal.valueOf(50))
                    .build();

    Authentication authentication =
            mock(Authentication.class);

    when(authentication.getPrincipal())
            .thenReturn(user);

    SecurityContextHolder.getContext()
            .setAuthentication(authentication);

    when(walletRepository.findByUserIdWithLock(1L))
            .thenReturn(Optional.of(wallet));

    WalletResponse response =
            walletService.addMoney(req);

    assertEquals(
            BigDecimal.valueOf(60),
            response.getBalance());

    verify(walletRepository)
            .save(wallet);
}

@Test
void addMoney_ShouldThrow_WhenAmountLessThan1() {

    AddMoneyRequest req =
            AddMoneyRequest.builder()
                    .amount(BigDecimal.ZERO)
                    .build();

    User user = User.builder()
            .id(1L)
            .build();

    Wallet wallet = Wallet.builder()
            .user(user)
            .balance(BigDecimal.TEN)
            .build();

    Authentication authentication =
            mock(Authentication.class);

    when(authentication.getPrincipal())
            .thenReturn(user);

    SecurityContextHolder.getContext()
            .setAuthentication(authentication);

    when(walletRepository.findByUserIdWithLock(1L))
            .thenReturn(Optional.of(wallet));

    assertThrows(
            IllegalArgumentException.class,
            () -> walletService.addMoney(req));
}

@Test
void addMoney_ShouldThrow_WhenAmountGreaterThan100() {

    AddMoneyRequest req =
            AddMoneyRequest.builder()
                    .amount(BigDecimal.valueOf(101))
                    .build();

    User user = User.builder()
            .id(1L)
            .build();

    Wallet wallet = Wallet.builder()
            .user(user)
            .balance(BigDecimal.TEN)
            .build();

    Authentication authentication =
            mock(Authentication.class);

    when(authentication.getPrincipal())
            .thenReturn(user);

    SecurityContextHolder.getContext()
            .setAuthentication(authentication);

    when(walletRepository.findByUserIdWithLock(1L))
            .thenReturn(Optional.of(wallet));

    assertThrows(
            IllegalArgumentException.class,
            () -> walletService.addMoney(req));
}

@Test
void withdrawMoney_ShouldDecreaseBalance() {

    User user = User.builder()
            .id(1L)
            .build();

    Wallet wallet = Wallet.builder()
            .user(user)
            .balance(BigDecimal.valueOf(100))
            .build();

    WithdrawRequest req =
            WithdrawRequest.builder()
                    .amount(BigDecimal.valueOf(20))
                    .build();

    Authentication authentication =
            mock(Authentication.class);

    when(authentication.getPrincipal())
            .thenReturn(user);

    SecurityContextHolder.getContext()
            .setAuthentication(authentication);

    when(walletRepository.findByUserIdWithLock(1L))
            .thenReturn(Optional.of(wallet));

    WalletResponse response =
            walletService.withdrawMoney(req);

    assertEquals(
            BigDecimal.valueOf(80),
            response.getBalance());

    verify(walletRepository)
            .save(wallet);
}

@Test
void withdrawMoney_ShouldThrow_WhenInsufficientFunds() {

    User user = User.builder()
            .id(1L)
            .build();

    Wallet wallet = Wallet.builder()
            .user(user)
            .balance(BigDecimal.valueOf(20))
            .build();

    WithdrawRequest req =
            WithdrawRequest.builder()
                    .amount(BigDecimal.valueOf(100))
                    .build();

    Authentication authentication =
            mock(Authentication.class);

    when(authentication.getPrincipal())
            .thenReturn(user);

    SecurityContextHolder.getContext()
            .setAuthentication(authentication);

    when(walletRepository.findByUserIdWithLock(1L))
            .thenReturn(Optional.of(wallet));

    assertThrows(
            InsufficientFundsException.class,
            () -> walletService.withdrawMoney(req));
}

}