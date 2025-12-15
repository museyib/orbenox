package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.summary.PriceSummary;
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
                concat(p.unit.code, ':', p.priceList.code) as priceKey,
                p.product as product,
                p.priceList as priceList,
                p.unit as unit,
                p.price as price,
                p.factorToParent as factorToParent,
                p.fixedPrice as fixedPrice,
                p.priceList.parent.id as parentId
            FROM ProductPriceList p
            WHERE p.product.id = :productId""")
    List<PriceSummary> getPriceSummariesByProductId(@Param("productId") Long productId);
}
