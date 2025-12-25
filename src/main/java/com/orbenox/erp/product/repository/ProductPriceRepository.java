package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.entity.ProductPrice;
import com.orbenox.erp.product.projection.ProductPriceItem;
import com.orbenox.erp.product.projection.SimpleProductItem;
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
                pp as parent
            FROM ProductPrice p
            LEFT JOIN p.priceList.parent pp
            WHERE p.product.id = :productId""")
    List<ProductPriceItem> getItemsByProductId(@Param("productId") Long productId);

    @Query("""
            SELECT p.id as id, p.code as code, p.name as name from Product p
            WHERE p.id = :productId and p.deleted = false""")
    SimpleProductItem getProductItemByProductId(@Param("productId") Long productId);
}
