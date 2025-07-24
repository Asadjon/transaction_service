package org.cyber_pantera.transaction_service.service;

import lombok.RequiredArgsConstructor;
import org.cyber_pantera.transaction_service.dto.*;
import org.cyber_pantera.transaction_service.entity.Status;
import org.cyber_pantera.transaction_service.entity.Transaction;
import org.cyber_pantera.transaction_service.entity.TransactionType;
import org.cyber_pantera.transaction_service.exception.TransactionException;
import org.cyber_pantera.transaction_service.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BalanceService balanceService;
    private final UserService userService;

    public CompletableFuture<List<TransactionResponse>> getAll() {
        return CompletableFuture.supplyAsync(() ->
                transactionRepository.findAll().stream()
                        .map(this::mapToResponse).toList());
    }

    public CompletableFuture<List<TransactionResponse>> getByUser(long userId) {
        return CompletableFuture.supplyAsync(() ->
                transactionRepository.findAllByFromUserId(userId).stream()
                        .map(this::mapToResponse).toList());
    }

    public CompletableFuture<List<TransactionResponse>> getTransactionsBetween(LocalDate from, LocalDate to) {
        if (from.isBefore(to))
            throw new IllegalArgumentException("from must be before to");

        if (from.isEqual(to))
            to = to.plusDays(1);

        var finalTo = to;
        return CompletableFuture.supplyAsync(()->
                transactionRepository.findByCreatedAtBetween(from.atStartOfDay(), finalTo.atStartOfDay()).stream()
                        .map(this::mapToResponse).toList());
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getFromUserId(),
                transaction.getToUserId(),
                transaction.getAmount(),
                transaction.getCreatedAt(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getDescription()
        );
    }

    public CompletableFuture<String> transfer(TransferRequest request) {
        var fromUserFuture = userService.validateUser(request.getFromUserId());
        var toUserFuture = userService.validateUser(request.getToUserId());
        var fromUser = new AtomicReference<UserResponse>();
        var toUser = new AtomicReference<UserResponse>();

        return CompletableFuture.allOf(fromUserFuture, toUserFuture)

                .thenAccept(unused -> {
                    fromUser.set(fromUserFuture.join());
                    toUser.set(toUserFuture.join());
                })

                .thenCompose(unused -> {
                    if (fromUser.get().getId() == toUser.get().getId())
                        return saveTransaction(fromUser.get().getId(), toUser.get().getId(), request.getAmount(),
                                TransactionType.TRANSFER, Status.FAILED, "From user id must be different to user id")
                                .thenApply(unused1 -> {
                                    throw new TransactionException("From user id must be different to user id");
                                });

                    return balanceService.getUserBalance(fromUser.get().getId());
                })

                .thenCompose(balance -> {
                    if (balance.getCurrent_balance().compareTo(request.getAmount()) < 0)
                        return saveTransaction(fromUser.get().getId(), toUser.get().getId(), request.getAmount(),
                                TransactionType.TRANSFER, Status.FAILED, "Amount must be greater than current balance")
                                .thenApply(unused -> {
                                    throw new TransactionException("Amount must be greater than current balance");
                                });

                   return CompletableFuture.allOf(balanceService.decrease(request.getFromUserId(), request.getAmount()),
                           balanceService.increase(request.getToUserId(), request.getAmount()));
                })

                .thenCompose(unused ->
                        saveTransaction(fromUser.get().getId(), toUser.get().getId(), request.getAmount(),
                                TransactionType.TRANSFER, Status.SUCCESS, "Transfer successful")
                                .thenApply(unused1 -> "Transfer successful"));
    }

    public CompletableFuture<String> withdraw(WithdrawRequest request) {
        return userService.validateUser(request.getUserId())

                .thenCompose(user -> balanceService.getUserBalance(user.getId()))

                .thenCompose(balance -> {
                    if (balance.getCurrent_balance().compareTo(request.getAmount()) < 0)
                        return saveTransaction(request.getUserId(), -1, request.getAmount(),
                                TransactionType.WITHDRAW, Status.FAILED, "Amount must be greater than current balance")
                                .thenApply(unused -> {
                                    throw new TransactionException("Amount must be greater than current balance");
                                });
                    return balanceService.decrease(request.getUserId(), request.getAmount());
                })

                .thenCompose(unused -> saveTransaction(request.getUserId(), -1, request.getAmount(),
                        TransactionType.WITHDRAW, Status.SUCCESS, "Withdrawal successful")
                        .thenApply(unused1 -> "Withdrawal successful"));
    }

    public CompletableFuture<String> deposit(DepositRequest request) {
        return userService.validateUser(request.getUserId())

                .thenCompose(user -> balanceService.increase(request.getUserId(), request.getAmount()))

                .thenCompose(unused -> saveTransaction(-1, request.getUserId(), request.getAmount(),
                        TransactionType.DEPOSIT, Status.SUCCESS, "Deposit successful")
                        .thenApply(unused1 -> "Deposit successful"));
    }

    private CompletableFuture<Void> saveTransaction(long fromUserId, long toUserId, BigDecimal amount, TransactionType type, Status status, String description) {
        var transaction = Transaction.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .amount(amount)
                .type(type)
                .status(status)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();

        return CompletableFuture.runAsync(() -> transactionRepository.save(transaction));
    }
}
