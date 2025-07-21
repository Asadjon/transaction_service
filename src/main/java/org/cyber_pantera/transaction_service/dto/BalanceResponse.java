package org.cyber_pantera.transaction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BalanceResponse {

    private long user_id;
    private BigDecimal current_balance;
    private LocalDateTime last_updated;
}
