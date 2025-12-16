package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.summary.PriceLineSummary;
import com.orbenox.erp.product.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    ProductPrice findByProductIdAndPriceListIdAndUnitId(Long productId, Long priceListId, Long unitId);

    @Query("""
            SELECT p.id as id,
                p.product as product,
                p.priceList as priceList,
                p.unit as unit,
                p.price as price,
                p.factorToParent as factorToParent,
                p.fixedPrice as fixedPrice,
                p.priceList.parent.id as parentId
            FROM ProductPrice p
            WHERE p.product.id = :productId""")
    List<PriceLineSummary> getSummariesByProductId(@Param("productId") Long productId);
}
