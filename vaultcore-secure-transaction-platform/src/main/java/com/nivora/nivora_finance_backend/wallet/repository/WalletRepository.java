package com.nivora.nivora_finance_backend.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nivora.nivora_finance_backend.wallet.entity.Wallet;

import jakarta.persistence.LockModeType;

public interface WalletRepository
        extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId")
    Optional<Wallet> findByUserIdWithLock(
            @Param("userId") Long userId);
}
