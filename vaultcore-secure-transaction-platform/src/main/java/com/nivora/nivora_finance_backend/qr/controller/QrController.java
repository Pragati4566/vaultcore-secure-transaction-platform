package com.nivora.nivora_finance_backend.qr.controller;

import org.springframework.web.bind.annotation.*;

import com.nivora.nivora_finance_backend.qr.dto.request.QrPaymentRequest;
import com.nivora.nivora_finance_backend.qr.dto.request.ResolveQrRequest;
import com.nivora.nivora_finance_backend.qr.dto.response.GenerateQrResponse;
import com.nivora.nivora_finance_backend.qr.dto.response.ResolveQrResponse;
import com.nivora.nivora_finance_backend.qr.service.QrService;
import com.nivora.nivora_finance_backend.transaction.dto.response.TransactionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Tag(name = "QR Payments", description = "Generate QR codes, resolve QR codes and transfer money via QR")
@RestController
@RequestMapping("/api/v1/qr")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class QrController {

    private final QrService qrService;

    @Operation(summary = "Generate My QR", description = """
            Generates a QR payload for the authenticated user.

            Example:
            nivora://user/1
            """)

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "QR generated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/my-qr")
    public GenerateQrResponse generateMyQr() {

        return qrService.generateMyQr();
    }

    @Operation(summary = "Resolve QR", description = """
            Resolves QR data and returns receiver details.

            Example QR:
            nivora://user/1
            """)

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "QR resolved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid QR code"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/resolve")
    public ResolveQrResponse resolveQr(
            @Valid @RequestBody ResolveQrRequest request) {

        return qrService.resolveQr(request);
    }

    @Operation(summary = "Pay Via QR", description = """
            Transfers money using QR data.

            Features:
            • Idempotency Protection
            • Balance Validation
            • Receiver Validation
            • Transaction Tracking
            • Reuses Transfer Service
            """)

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/pay")
    public TransactionResponse payViaQr(
            @Valid @RequestBody QrPaymentRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        return qrService.payViaQr(
                request,
                idempotencyKey);
    }
}