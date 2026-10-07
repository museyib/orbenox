package com.orbenox.erp.repository;

import com.orbenox.erp.entity.StockBalance;
import com.orbenox.erp.projection.StockBalanceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface StockBalanceRepository extends JpaRepository<StockBalance, Long> {

    @Query("""
            SELECT s.productId as productId,
                s.warehouseId as warehouseId,
                s.quantity as quantity,
                s.reservedQuantity as reservedQuantity,
                s.freeQuantity as freeQuantity
            FROM StockBalance s
            WHERE s.productId = :productId""")
    List<StockBalanceItem> getStockBalanceItems(@Param("productId") Long productId);

    @Query("""
            SELECT s.productId as productId,
                s.warehouseId as warehouseId,
                s.quantity as quantity,
                s.reservedQuantity as reservedQuantity,
                s.freeQuantity as freeQuantity
            FROM StockBalance s
            WHERE s.productId = :productId AND s.warehouseId = :warehouseId""")
    Optional<StockBalanceItem> getStockBalanceItem(Long productId, Long warehouseId);

    @Query(value = """
            INSERT INTO stock_balance (product_id, warehouse_id, quantity)
                    VALUES (:productId, :warehouseId, :quantity)
            ON CONFLICT (product_id, warehouse_id) DO
            UPDATE SET quantity = stock_balance.quantity + EXCLUDED.quantity
            """, nativeQuery = true)
    @Modifying
    int increaseQuantity(@Param("productId") Long productId,
                         @Param("warehouseId") Long warehouseId,
                         @Param("quantity") BigDecimal quantity);

    @Query(value = """
            UPDATE stock_balance
            SET quantity = quantity - :quantity
            WHERE product_id = :productId
                AND warehouse_id = :warehouseId
                AND quantity >= :quantity
            """, nativeQuery = true)
    @Modifying
    int decreaseQuantity(@Param("productId") Long productId,
                         @Param("warehouseId") Long warehouseId,
                         @Param("quantity") BigDecimal quantity);
}
