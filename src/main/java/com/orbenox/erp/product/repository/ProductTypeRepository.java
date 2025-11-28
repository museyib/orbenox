package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
    List<ProductType> findAllByDeletedFalse();

    ProductType findByIdAndDeletedFalse(Long id);
}
