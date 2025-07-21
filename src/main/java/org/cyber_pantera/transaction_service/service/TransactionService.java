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

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BalanceService balanceService;
    private final UserService userService;

    public List<TransactionResponse> getAll() {
        return transactionRepository.findAll().stream()
                .map(this::mapToResponse).toList();
    }

    public List<TransactionResponse> getByUser(long userId) {
        var transactions = transactionRepository.findAllByFromUserId(userId).stream()
                .map(this::mapToResponse).toList();

        if (transactions.isEmpty())
            throw new TransactionException("User transactions not found");

        return transactions;
    }

    public List<TransactionResponse> getTransactionsBetween(LocalDate from, LocalDate to) {
        if (from.isBefore(to))
            throw new IllegalArgumentException("from must be before to");

        if (from.isEqual(to))
            to = to.plusDays(1);

        return transactionRepository.findByCreatedAtBetween(from.atStartOfDay(), to.atStartOfDay()).stream()
                .map(this::mapToResponse).toList();
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

    public String transfer(TransferRequest request) {
        var fromUser = userService.validateUser(request.getFromUserId());
        var toUser = userService.validateUser(request.getToUserId());

        if (fromUser.getId() == toUser.getId()) {

            saveTransaction(fromUser.getId(), toUser.getId(), request.getAmount(),
                    TransactionType.TRANSFER, Status.FAILED, "From user id must be different to user id");

            throw new TransactionException("From user id must be different to user id");
        }

        var balance = balanceService.getUserBalance(fromUser.getId());

        if (balance.getCurrent_balance().compareTo(request.getAmount()) < 0) {

            saveTransaction(fromUser.getId(), toUser.getId(), request.getAmount(),
                    TransactionType.TRANSFER, Status.FAILED, "Amount must be greater than current balance");

            throw new TransactionException("Amount must be greater than current balance");
        }

        balanceService.decrease(request.getFromUserId(), request.getAmount());
        balanceService.increase(request.getToUserId(), request.getAmount());

        saveTransaction(fromUser.getId(), toUser.getId(), request.getAmount(),
                TransactionType.TRANSFER, Status.SUCCESS, "Transfer successful");

        return "Transfer successful";
    }

    public String withdraw(WithdrawRequest request) {
        userService.validateUser(request.getUserId());

        var userBalance = balanceService.getUserBalance(request.getUserId());
        if (userBalance.getCurrent_balance().compareTo(request.getAmount()) < 0) {

            saveTransaction(request.getUserId(), -1, request.getAmount(), TransactionType.WITHDRAW, Status.FAILED, "Amount must be greater than current balance");

            throw new TransactionException("Amount must be greater than current balance");
        }

        balanceService.decrease(request.getUserId(), request.getAmount());
        saveTransaction(request.getUserId(), -1, request.getAmount(), TransactionType.WITHDRAW, Status.SUCCESS, "Withdrawal successful");

        return "Withdrawal successful";
    }

    public String deposit(DepositRequest request) {
        userService.validateUser(request.getUserId());

        balanceService.increase(request.getUserId(), request.getAmount());
        saveTransaction(-1, request.getUserId(), request.getAmount(), TransactionType.DEPOSIT, Status.SUCCESS, "Deposit successful");

        return "Deposit successful";
    }

    private void saveTransaction(long fromUserId, long toUserId, BigDecimal amount, TransactionType type, Status status, String description) {
        var transaction = Transaction.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .amount(amount)
                .type(type)
                .status(status)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);
    }
}
