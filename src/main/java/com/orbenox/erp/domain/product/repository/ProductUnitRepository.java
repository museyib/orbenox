package com.orbenox.erp.domain.product.repository;

import com.orbenox.erp.domain.product.entity.ProductUnit;
import com.orbenox.erp.domain.product.projection.ProductUnitItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductUnitRepository extends JpaRepository<ProductUnit, Long> {

    @Query("""
        SELECT u.id as id,
                u.product as product,
                u.unit as unit,
                u.factorToBase as factorToBase
        FROM ProductUnit u
        WHERE u.product.id = :productId
        ORDER BY u.id""")
    List<ProductUnitItem> getItemsByProductId(@Param("productId") Long productId);
}
