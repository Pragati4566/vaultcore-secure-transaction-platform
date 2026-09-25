package com.nivora.nivora_finance_backend.qr.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nivora.nivora_finance_backend.auth.entity.User;
import com.nivora.nivora_finance_backend.auth.repository.UserRepository;
import com.nivora.nivora_finance_backend.common.exception.ResourceNotFoundException;
import com.nivora.nivora_finance_backend.qr.dto.request.QrPaymentRequest;
import com.nivora.nivora_finance_backend.qr.dto.request.ResolveQrRequest;
import com.nivora.nivora_finance_backend.qr.dto.response.GenerateQrResponse;
import com.nivora.nivora_finance_backend.qr.dto.response.ResolveQrResponse;
import com.nivora.nivora_finance_backend.qr.service.impl.QrServiceImpl;
import com.nivora.nivora_finance_backend.transaction.dto.request.TransferRequest;
import com.nivora.nivora_finance_backend.transaction.dto.response.TransactionResponse;
import com.nivora.nivora_finance_backend.transaction.service.TransactionService;

@ExtendWith(MockitoExtension.class)
class QrServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private QrServiceImpl qrService;

    @Test
    void generateMyQr_ShouldReturnQrData() {

        User user = User.builder()
                .id(1L)
                .name("Harsh")
                .build();

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.getPrincipal())
                .thenReturn(user);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        GenerateQrResponse response =
                qrService.generateMyQr();

        assertEquals(
                "nivora://user/1",
                response.getQrData());
    }

    @Test
    void resolveQr_ShouldReturnUser() {

        User user = User.builder()
                .id(1L)
                .name("Harsh")
                .build();

        ResolveQrRequest request =
                new ResolveQrRequest();

        request.setQrData(
                "nivora://user/1");

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        ResolveQrResponse response =
                qrService.resolveQr(request);

        assertEquals(
                1L,
                response.getUserId());
    }

    @Test
    void resolveQr_ShouldThrow_WhenInvalidQr() {

        ResolveQrRequest request =
                new ResolveQrRequest();

        request.setQrData(
                "invalid");

        assertThrows(
                IllegalArgumentException.class,
                () -> qrService.resolveQr(request));
    }

    @Test
    void payViaQr_ShouldCallTransferService() {

        QrPaymentRequest request =
                new QrPaymentRequest();

        request.setQrData(
                "nivora://user/2");

        request.setAmount(
                BigDecimal.valueOf(50));

        TransactionResponse response =
                TransactionResponse.builder()
                        .transactionId(1L)
                        .build();

        when(transactionService.transferMoney(
                any(TransferRequest.class),
                eq("abc123")))
                .thenReturn(response);

        TransactionResponse result =
                qrService.payViaQr(
                        request,
                        "abc123");

        assertEquals(
                1L,
                result.getTransactionId());

        verify(transactionService)
                .transferMoney(
                        any(TransferRequest.class),
                        eq("abc123"));
    }
}