package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.projection.ProductPriceItem;
import com.orbenox.erp.product.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
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
    List<ProductPriceItem> getItemsByProductId(@Param("productId") Long productId);

    @Query("""
        SELECT p.id as id, p.code as code, p.name as name from Product p
        WHERE p.id = :productId and p.deleted = false""")
    ProductPriceItem.Product getProductItemByProductId(@Param("productId") Long productId);
}
