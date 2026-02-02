package com.orbenox.erp.transaction.repository;

import com.orbenox.erp.transaction.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
}
