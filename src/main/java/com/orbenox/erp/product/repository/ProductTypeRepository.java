package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.entity.ProductType;
import com.orbenox.erp.product.projection.ProductTypeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {

    ProductType findByIdAndDeletedFalse(Long id);

    @Query("""
            SELECT p.id as id,
                    p.code as code,
                    p.name as name,
                    p.description as description,
                    p.enabled as enabled
                FROM ProductType p
            WHERE p.deleted = false
            ORDER BY p.id""")
    List<ProductTypeItem> getAllItems();


    @Query("""
            SELECT p.id as id,
                    p.code as code,
                    p.name as name,
                    p.description as description,
                    p.enabled as enabled
                FROM ProductType p
            WHERE p.id = :id AND p.deleted = false
            ORDER BY p.id""")
    ProductTypeItem getItemById(@Param("id") Long id);
}
