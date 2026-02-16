package com.orbenox.erp.transaction.repository;

import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.warehouse.Warehouse;
import com.orbenox.erp.transaction.entity.StockBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockBalanceRepository extends JpaRepository<StockBalance, Long> {
    Optional<StockBalance> findByProductAndWarehouse(Product product, Warehouse warehouse);
}
