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
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/all")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByUserId(@PathVariable long userId) {
        return ResponseEntity.ok(transactionService.getByUser(userId));
    }

    @GetMapping("/range")
    public ResponseEntity<List<TransactionResponse>> getTransactionsInRange(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(transactionService.getTransactionsBetween(from, to));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferTransactions(@RequestBody TransferRequest request) {
        return ResponseEntity.ok(transactionService.transfer(request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdrawTransactions(@RequestBody WithdrawRequest request) {
        return ResponseEntity.ok(transactionService.withdraw(request));
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> depositTransactions(@RequestBody DepositRequest request) {
        return ResponseEntity.ok(transactionService.deposit(request));
    }
}
