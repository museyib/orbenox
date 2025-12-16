package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.entity.Product;
import com.orbenox.erp.product.summary.ProductSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByDeletedFalseOrderByIdAsc();

    Product findByIdAndDeletedFalse(Long id);

    @Query("""
        SELECT p.id as id, p.code as code, p.name as name from Product p
        WHERE p.id = :productId and p.deleted = false""")
    ProductSummary getProductSummaryByProductId(@Param("productId") Long productId);
    @Query("""
        SELECT p.id as id,
            p.code as code,
            p.name as name,
            p.brand as brand
        FROM Product p
        WHERE p.deleted = false""")
    List<ProductSummary> getAllItems();
}
