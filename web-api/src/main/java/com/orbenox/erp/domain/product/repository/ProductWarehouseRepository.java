package com.orbenox.erp.domain.product.repository;

import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.entity.ProductWarehouse;
import com.orbenox.erp.domain.product.projection.ProductWarehouseItem;
import com.orbenox.erp.domain.warehouse.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductWarehouseRepository extends JpaRepository<ProductWarehouse, Long> {

    @Query("""
        SELECT p.id as id,
               p.product as product,
               p.warehouse as warehouse,
               p.quantity as quantity,
               p.reservedQuantity as reservedQuantity,
               p.freeQuantity as freeQuantity,
               p.minQuantity as minQuantity,
               p.maxQuantity as maxQuantity
        FROM ProductWarehouse p
        WHERE p.product.id = :productId
        ORDER BY p.id""")
    List<ProductWarehouseItem> getItemsByProductId(@Param("productId") Long productId);

    Optional<ProductWarehouse> findByProductAndWarehouse(Product product, Warehouse warehouse);

    ProductWarehouse findByProductIdAndWarehouseId(Long productId, Long warehouseId);
}
