package org.cyber_pantera.transaction_service.handler;

import org.cyber_pantera.transaction_service.exception.BalanceException;
import org.cyber_pantera.transaction_service.exception.TransactionException;
import org.cyber_pantera.transaction_service.exception.UserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<?> transactionException(TransactionException e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> userException(UserException e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(BalanceException.class)
    public ResponseEntity<?> balanceException(BalanceException e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }
}
