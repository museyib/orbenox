package com.orbenox.erp.stockservice.repository;

import com.orbenox.erp.stockservice.entity.StockContext;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockContextRepository extends JpaRepository<StockContext, Long> {
}
