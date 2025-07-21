package org.cyber_pantera.transaction_service.repository;

import org.cyber_pantera.transaction_service.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByFromUserId(long userId);

    List<Transaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
