package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByDeletedFalse();

    Product findByIdAndDeletedFalse(Long id);
}
