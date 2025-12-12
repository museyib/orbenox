package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.entity.ProductPriceList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductPriceListRepository extends JpaRepository<ProductPriceList, Long> {
    List<ProductPriceList> findAllByProductId(Long productId);

    ProductPriceList findByProductIdAndPriceListIdAndUnitId(Long productId, Long priceListId, Long unitId);
}
