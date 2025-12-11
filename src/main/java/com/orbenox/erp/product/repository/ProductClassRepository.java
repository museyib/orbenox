package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.entity.ProductClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductClassRepository extends JpaRepository<ProductClass, Long> {
    List<ProductClass> findAllByDeletedFalseOrderByIdAsc();

    ProductClass findByIdAndDeletedFalse(Long id);
}
