package com.orbenox.erp.domain.stock;

import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.warehouse.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockBalanceRepository extends JpaRepository<StockBalance, Long> {

    @Query("""
        SELECT s.product as product,
            s.warehouse as warehouse,
            s.quantity as quantity,
            s.reservedQuantity as reservedQuantity,
            s.freeQuantity as freeQuantity
        FROM StockBalance s
        WHERE s.product.id = :productId""")
    List<StockBalanceItem> getItemsByProductId(@Param("productId") Long productId);
    Optional<StockBalance> findByProductAndWarehouse(Product product, Warehouse warehouse);
}
