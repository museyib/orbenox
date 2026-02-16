package com.orbenox.erp.domain.product.repository;

import com.orbenox.erp.domain.product.entity.ProductCategory;
import com.orbenox.erp.domain.product.projection.ProductCategoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    ProductCategory findByIdAndDeletedFalse(Long id);

    @Query("""
            SELECT p.id as id,
                    p.code as code,
                    p.name as name,
                    p.description as description,
                    p.enabled as enabled
                FROM ProductCategory p
            WHERE p.deleted = false
            ORDER BY p.id""")
    List<ProductCategoryItem> getAllItems();

    @Query("""
            SELECT p.id as id,
                    p.code as code,
                    p.name as name,
                    p.description as description,
                    p.enabled as enabled
                FROM ProductCategory p
            WHERE p.deleted = false AND p.enabled = true
            ORDER BY p.id""")
    List<ProductCategoryItem> getEnabledItems();

    @Query("""
            SELECT p.id as id,
                    p.code as code,
                    p.name as name,
                    p.description as description,
                    p.enabled as enabled
                FROM ProductCategory p
            WHERE p.id = :id AND p.deleted = false
            ORDER BY p.id""")
    ProductCategoryItem getItemById(@Param("id") Long id);
}
