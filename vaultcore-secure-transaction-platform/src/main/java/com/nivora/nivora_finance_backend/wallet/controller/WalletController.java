package com.nivora.nivora_finance_backend.wallet.controller;

import com.nivora.nivora_finance_backend.wallet.dto.request.AddMoneyRequest;
import com.nivora.nivora_finance_backend.wallet.dto.request.WithdrawRequest;
import com.nivora.nivora_finance_backend.wallet.dto.response.BalanceResponse;
import com.nivora.nivora_finance_backend.wallet.dto.response.WalletResponse;
import com.nivora.nivora_finance_backend.wallet.service.WalletService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Wallet Management",
        description = "Wallet balance management, deposits and withdrawals"
)
@RestController
@RequestMapping("/api/v1/wallet")
@AllArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @Operation(
            summary = "Get wallet balance",
            description = "Returns current authenticated user's wallet balance."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @GetMapping("/balance")
    public BalanceResponse getBalance() {
        return walletService.getBalance();
    }

    @Operation(
            summary = "Add money",
            description = """
                    Credits money to wallet.

                    Rules:
                    • Minimum amount = 1
                    • Maximum amount = 100
                    • Protected against concurrent updates
                    """
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Money added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid amount"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PostMapping("/add-money")
    public WalletResponse addMoney(
        @Valid @RequestBody AddMoneyRequest req) {

        return walletService.addMoney(req);
    }

    @Operation(
            summary = "Withdraw money",
            description = """
                    Debits money from wallet.

                    Rules:
                    • Minimum amount = 1
                    • Maximum amount = 100
                    • Insufficient balance validation
                    • Protected against concurrent updates
                    """
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Money withdrawn successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid amount"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "409", description = "Insufficient wallet balance")
    })
    @PostMapping("/withdraw")
    public WalletResponse withdrawMoney(
        @Valid @RequestBody WithdrawRequest req) {

        return walletService.withdrawMoney(req);
    }
}