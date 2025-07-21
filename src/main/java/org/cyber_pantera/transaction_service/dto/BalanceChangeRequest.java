package org.cyber_pantera.transaction_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BalanceChangeRequest {

    private long userId;
    private BigDecimal amount;
    private ChangeType type;
}

