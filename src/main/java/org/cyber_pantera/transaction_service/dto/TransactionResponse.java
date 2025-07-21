package org.cyber_pantera.transaction_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.cyber_pantera.transaction_service.entity.Status;
import org.cyber_pantera.transaction_service.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionResponse {
    private long fromUserId;
    private long toUserId;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private TransactionType type;
    private Status status;
    private String description;
}
