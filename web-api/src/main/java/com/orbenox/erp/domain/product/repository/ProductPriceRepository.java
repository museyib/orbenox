package com.orbenox.erp.domain.product.repository;

import com.orbenox.erp.domain.price.PriceList;
import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.entity.ProductPrice;
import com.orbenox.erp.domain.product.projection.ProductPriceItem;
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
                p.roundLength as roundLength,
                p.discountRatioLimit as discountRatioLimit,
                pp as parent
            FROM ProductPrice p
            LEFT JOIN p.priceList.parent pp
            WHERE p.product.id = :productId
            ORDER BY p.unit.id, coalesce(pp.id, p.priceList.id) , p.priceList.id""")
    List<ProductPriceItem> getItemsByProductId(@Param("productId") Long productId);

    ProductPrice findByProductAndPriceList(Product product, PriceList priceList);
    @Query("""
            SELECT p.id as id,
                p.product as product,
                p.priceList as priceList,
                p.unit as unit,
                p.price as price,
                p.factorToParent as factorToParent,
                p.fixedPrice as fixedPrice,
                p.roundLength as roundLength,
                p.discountRatioLimit as discountRatioLimit
            FROM ProductPrice p
            WHERE p.product.id = :productId
                AND p.priceList.id = :priceListId
                AND p.unit.id = :unitId
            ORDER BY p.unit.id, p.priceList.id""")
    ProductPriceItem getByProductIdAndPriceListId(@Param("productId") Long productId,
                                                  @Param("priceListId") Long priceListId,
                                                  @Param("unitId") Long unitId);
}
