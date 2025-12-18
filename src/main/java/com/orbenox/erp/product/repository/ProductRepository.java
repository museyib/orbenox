package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.entity.Product;
import com.orbenox.erp.product.projection.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByDeletedFalseOrderByIdAsc();

    Product findByIdAndDeletedFalse(Long id);

    @Query("""
        SELECT p.id as id,
            p.code as code,
            p.name as name,
            p.brand as brand
        FROM Product p
        WHERE p.deleted = false""")
    List<ProductItem> getAllItems();
}
