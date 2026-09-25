package com.nivora.nivora_finance_backend.transaction.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import com.nivora.nivora_finance_backend.transaction.dto.request.TransferRequest;
import com.nivora.nivora_finance_backend.transaction.dto.response.RecentContactResponse;
import com.nivora.nivora_finance_backend.transaction.dto.response.TransactionResponse;
import com.nivora.nivora_finance_backend.transaction.dto.response.TransactionSummaryResponse;
import com.nivora.nivora_finance_backend.transaction.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Tag(name = "Transactions", description = "Money transfers, transaction history, summaries and recent contacts")
@RestController
@RequestMapping("/api/v1/transactions")
@AllArgsConstructor
public class TransactionController {

        private final TransactionService transactionService;

        @Operation(summary = "Transfer money", description = """
                        Transfers money between users.

                        Features:
                        • Idempotency Protection
                        • Balance Validation
                        • Self Transfer Prevention
                        • Receiver Validation
                        • Transaction Tracking
                        """)
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Transfer successful"),
                        @ApiResponse(responseCode = "400", description = "Invalid transfer request"),
                        @ApiResponse(responseCode = "401", description = "User not authenticated"),
                        @ApiResponse(responseCode = "404", description = "Receiver not found"),
                        @ApiResponse(responseCode = "409", description = "Insufficient balance")
        })
        @PostMapping("/transfer")
        public TransactionResponse transferMoney(
        @Valid @RequestBody TransferRequest req,
        @RequestHeader("Idempotency-Key") String idempotencyKey) {

                return transactionService.transferMoney(
                                req,
                                idempotencyKey);
        }

        @Operation(summary = "Get transactions", description = "Returns authenticated user's transaction history.")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "User not authenticated")
        })
        @GetMapping
        public Page<TransactionResponse> getMyTransactions(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size) {

                return transactionService.getMyTransactions(
                                page,
                                size);
        }

        @Operation(summary = "Get transaction details", description = "Returns complete transaction information by transaction id.")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Transaction found"),
                        @ApiResponse(responseCode = "401", description = "User not authenticated"),
                        @ApiResponse(responseCode = "404", description = "Transaction not found")
        })
        @GetMapping("/{transactionId}")
        public TransactionResponse getTransactionById(
                        @PathVariable Long transactionId) {

                return transactionService.getTransactionById(
                                transactionId);
        }

        @Operation(summary = "Search transactions", description = "Search transactions by status, type or transaction id.")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
                        @ApiResponse(responseCode = "401", description = "User not authenticated")
        })
        @GetMapping("/search")
        public List<TransactionResponse> searchTransactions(
                        @RequestParam String q) {

                return transactionService.searchTransactions(q);
        }

        @Operation(summary = "Transaction summary", description = "Returns total sent, total received and transaction statistics.")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Summary generated successfully"),
                        @ApiResponse(responseCode = "401", description = "User not authenticated")
        })
        @GetMapping("/summary")
        public TransactionSummaryResponse getSummary() {

                return transactionService.getSummary();
        }

        @Operation(summary = "Recent contacts", description = "Returns users recently involved in transfers.")
        @SecurityRequirement(name = "bearerAuth")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Contacts retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "User not authenticated")
        })
        @GetMapping("/recent-contacts")
        public List<RecentContactResponse> getRecentContacts() {

                return transactionService.getRecentContacts();
        }
}