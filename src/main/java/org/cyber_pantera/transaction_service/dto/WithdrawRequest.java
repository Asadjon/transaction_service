package org.cyber_pantera.transaction_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequest {
    private long userId;
    private BigDecimal amount;
}
