package com.orbenox.erp.domain.product.repository;

import com.orbenox.erp.domain.product.entity.ProductClass;
import com.orbenox.erp.domain.product.projection.ProductClassItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductClassRepository extends JpaRepository<ProductClass, Long> {

    ProductClass findByIdAndDeletedFalse(Long id);

    @Query("""
            SELECT p.id as id,
                    p.code as code,
                    p.name as name,
                    p.description as description,
                    p.enabled as enabled
                FROM ProductClass p
            WHERE p.deleted = false
            ORDER BY p.id""")
    Slice<ProductClassItem> getAllItems(Pageable pageable);

@Query("""
            SELECT p.id as id,
                    p.code as code,
                    p.name as name,
                    p.description as description,
                    p.enabled as enabled
                FROM ProductClass p
            WHERE  p.deleted = false
                    AND (LOWER(p.code) LIKE %:search% OR LOWER(p.name) LIKE %:search% OR LOWER(p.description) LIKE %:search%)
            ORDER BY p.id""")
    Slice<ProductClassItem> getItemsSearched(Pageable pageable, @Param("search") String search);

    @Query("""
            SELECT p.id as id,
                    p.code as code,
                    p.name as name,
                    p.description as description,
                    p.enabled as enabled
                FROM ProductClass p
            WHERE p.deleted = false AND p.enabled = true
            ORDER BY p.id""")
    List<ProductClassItem> getEnabledItems();

    @Query("""
            SELECT p.id as id,
                    p.code as code,
                    p.name as name,
                    p.description as description,
                    p.enabled as enabled
                FROM ProductClass p
            WHERE p.id = :id AND p.deleted = false
            ORDER BY p.id""")
    ProductClassItem getItemById(@Param("id") Long id);
}

