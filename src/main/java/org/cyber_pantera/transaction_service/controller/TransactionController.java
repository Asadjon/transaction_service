package org.cyber_pantera.transaction_service.controller;

import lombok.RequiredArgsConstructor;
import org.cyber_pantera.transaction_service.dto.DepositRequest;
import org.cyber_pantera.transaction_service.dto.TransactionResponse;
import org.cyber_pantera.transaction_service.dto.TransferRequest;
import org.cyber_pantera.transaction_service.dto.WithdrawRequest;
import org.cyber_pantera.transaction_service.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/all")
    public CompletableFuture<ResponseEntity<List<TransactionResponse>>> getAllTransactions() {
        return transactionService.getAll()
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/user/{userId}")
    public CompletableFuture<ResponseEntity<List<TransactionResponse>>> getTransactionsByUserId(@PathVariable long userId) {
        return transactionService.getByUser(userId)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/range")
    public CompletableFuture<ResponseEntity<List<TransactionResponse>>> getTransactionsInRange(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return transactionService.getTransactionsBetween(from, to)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/transfer")
    public CompletableFuture<ResponseEntity<String>> transferTransactions(@RequestBody TransferRequest request) {
        return transactionService.transfer(request)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/withdraw")
    public CompletableFuture<ResponseEntity<String>> withdrawTransactions(@RequestBody WithdrawRequest request) {
        return transactionService.withdraw(request)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/deposit")
    public CompletableFuture<ResponseEntity<String>> depositTransactions(@RequestBody DepositRequest request) {
        return transactionService.deposit(request)
                .thenApply(ResponseEntity::ok);
    }
}
