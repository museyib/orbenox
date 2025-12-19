package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.projection.ProductGroupItem;
import com.orbenox.erp.product.entity.ProductGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductGroupRepository extends JpaRepository<ProductGroup, Long> {
    ProductGroup findByIdAndDeletedFalse(Long id);

    @Query("""
        SELECT p.id as id,
                p.code as code,
                p.name as name,
                p.description as description,
                p.enabled as enabled,
                pp as parent
            FROM ProductGroup p
            LEFT JOIN p.parent as pp
        WHERE p.deleted = false
        ORDER BY p.id""")
    List<ProductGroupItem> getAllItems();

    @Query("""
        SELECT p.id as id,
                p.code as code,
                p.name as name,
                p.description as description,
                p.enabled as enabled,
                pp as parent
            FROM ProductGroup p
            LEFT JOIN p.parent as pp
        WHERE p.id = :id and p.deleted = false""")
    ProductGroupItem getItemById(@Param("id") Long id);


    @Query("""
            SELECT p.id as id,
                p.code as code,
                p.name as name,
                p.enabled as enabled
            FROM ProductGroup p
            WHERE p.id IN(:ids) AND p.deleted = false
            ORDER BY p.id""")
    List<ProductGroupItem.Parent> getParentItems(@Param("ids") List<Long> idsToExclude);
}
