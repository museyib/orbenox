package com.orbenox.erp.transaction.repository;

import com.orbenox.erp.transaction.entity.StockContext;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockContextRepository extends JpaRepository<StockContext, Long> {
}
