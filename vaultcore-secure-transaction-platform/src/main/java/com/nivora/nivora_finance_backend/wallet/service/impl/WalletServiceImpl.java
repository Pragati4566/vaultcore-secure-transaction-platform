package com.nivora.nivora_finance_backend.wallet.service.impl;

import java.math.BigDecimal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.nivora.nivora_finance_backend.auth.entity.User;
import com.nivora.nivora_finance_backend.common.exception.InsufficientFundsException;
import com.nivora.nivora_finance_backend.common.exception.ResourceNotFoundException;
import com.nivora.nivora_finance_backend.wallet.dto.request.AddMoneyRequest;
import com.nivora.nivora_finance_backend.wallet.dto.request.WithdrawRequest;
import com.nivora.nivora_finance_backend.wallet.dto.response.BalanceResponse;
import com.nivora.nivora_finance_backend.wallet.dto.response.WalletResponse;
import com.nivora.nivora_finance_backend.wallet.entity.Wallet;
import com.nivora.nivora_finance_backend.wallet.repository.WalletRepository;
import com.nivora.nivora_finance_backend.wallet.service.WalletService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

   @Override
public BalanceResponse getBalance() {
    User user = getCurrentUser();

    Wallet wallet = walletRepository.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

    return BalanceResponse.builder()
            .balance(wallet.getBalance())
            .build();
}

    @Override
    @Transactional
    public WalletResponse addMoney(AddMoneyRequest req) {

        User user = getCurrentUser();
        Wallet wallet = walletRepository.findByUserIdWithLock(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wallet not found"));

        if (req.getAmount().compareTo(BigDecimal.ONE) < 0) {
            throw new IllegalArgumentException(
                    "Minimum amount is $1");
        }

        if (req.getAmount().compareTo(
                BigDecimal.valueOf(100)) > 0) {

            throw new IllegalArgumentException(
                    "Maximum amount is $100");
        }

        wallet.setBalance(wallet.getBalance().add(req.getAmount()));

        walletRepository.save(wallet);

        return WalletResponse.builder()
                .message("Money added successfully")
                .balance(wallet.getBalance())
                .build();

    }

    @Override
    @Transactional
    public WalletResponse withdrawMoney(WithdrawRequest req) {
        User user = getCurrentUser();
        Wallet wallet = walletRepository.findByUserIdWithLock(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wallet not found"));

        if (req.getAmount().compareTo(
                BigDecimal.ONE) < 0) {

            throw new IllegalArgumentException(
                    "Minimum withdrawal amount is $1");
        }

        if (wallet.getBalance().compareTo(
                req.getAmount()) < 0) {

            throw new InsufficientFundsException(
                    "Insufficient wallet balance");
        }

        wallet.setBalance(
                wallet.getBalance()
                        .subtract(req.getAmount()));

        walletRepository.save(wallet);

        return WalletResponse.builder()
                .message("Money withdrawn successfully")
                .balance(wallet.getBalance())
                .build();

    }

    private User getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        return (User) authentication.getPrincipal();
    }

}
