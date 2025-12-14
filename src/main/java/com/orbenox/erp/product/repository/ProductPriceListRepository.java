package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.dto.PriceSummary;
import com.orbenox.erp.product.entity.ProductPriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductPriceListRepository extends JpaRepository<ProductPriceList, Long> {
    List<ProductPriceList> findAllByProductId(Long productId);

    ProductPriceList findByProductIdAndPriceListIdAndUnitId(Long productId, Long priceListId, Long unitId);

    @Query("""
            SELECT p.id as id,
                p.unit.id as unitId,
                p.unit.code as unitCode,
                p.priceList.id as priceListId,
                p.priceList.code as priceListCode,
                p.price as price,
                p.factorToParent as factorToParent,
                p.fixedPrice as fixedPrice
            FROM ProductPriceList p
            WHERE p.product.id = :productId""")
    List<PriceSummary> getPriceSummariesByProductId(@Param("productId") Long productId);
}
