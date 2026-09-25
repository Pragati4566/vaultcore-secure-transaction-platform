package com.nivora.nivora_finance_backend.qr.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.nivora.nivora_finance_backend.auth.entity.User;
import com.nivora.nivora_finance_backend.auth.repository.UserRepository;
import com.nivora.nivora_finance_backend.common.exception.ResourceNotFoundException;
import com.nivora.nivora_finance_backend.qr.dto.request.QrPaymentRequest;
import com.nivora.nivora_finance_backend.qr.dto.request.ResolveQrRequest;
import com.nivora.nivora_finance_backend.qr.dto.response.GenerateQrResponse;
import com.nivora.nivora_finance_backend.qr.dto.response.ResolveQrResponse;
import com.nivora.nivora_finance_backend.qr.service.QrService;
import com.nivora.nivora_finance_backend.transaction.dto.request.TransferRequest;
import com.nivora.nivora_finance_backend.transaction.dto.response.TransactionResponse;
import com.nivora.nivora_finance_backend.transaction.service.TransactionService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QrServiceImpl implements QrService {

    private final UserRepository userRepository;
    private final TransactionService transactionService;

    @Override
    public GenerateQrResponse generateMyQr() {

        User user = getCurrentUser();

        return GenerateQrResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .qrData("nivora://user/" + user.getId())
                .build();
    }

    @Override
    public ResolveQrResponse resolveQr(ResolveQrRequest request) {

        Long userId = extractUserId(
                request.getQrData());

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        return ResolveQrResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .build();
    }

    @Override
    public TransactionResponse payViaQr(
            QrPaymentRequest request,
            String idempotencyKey) {

        Long receiverId = extractUserId(
                request.getQrData());

        TransferRequest transferRequest =
                TransferRequest.builder()
                        .receiverId(receiverId)
                        .amount(request.getAmount())
                        .build();

        return transactionService.transferMoney(
                transferRequest,
                idempotencyKey);
    }

    private Long extractUserId(String qrData) {

        if (!qrData.startsWith("nivora://user/")) {
            throw new IllegalArgumentException("Invalid QR code");
        }

        try {
            return Long.parseLong(
                    qrData.replace("nivora://user/", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid QR code");
        }
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        return (User) authentication.getPrincipal();
    }
}