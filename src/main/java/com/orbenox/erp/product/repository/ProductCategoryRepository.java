package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findAllByDeletedFalse();

    ProductCategory findByIdAndDeletedFalse(Long id);
}
