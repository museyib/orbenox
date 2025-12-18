package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.projection.ProductGroupItem;
import com.orbenox.erp.product.entity.ProductGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductGroupRepository extends JpaRepository<ProductGroup, Long> {
    List<ProductGroup> findAllByDeletedFalseOrderByIdAsc();

    @EntityGraph(attributePaths = {"parent", "children"})
    ProductGroup findByIdAndDeletedFalse(Long id);

    @Query("""
        select g.id as id, g.name as name,
                coalesce(g.parent.id, 0) as parentId from ProductGroup g
        where g.deleted = false
        """)
    List<ProductGroupItem> getAllItems();


    @Query("""
            SELECT p.id as id,
                p.code as code,
                p.name as name,
                p.enabled as enabled
            FROM ProductGroup p
            WHERE p.id IN(:ids) AND p.deleted = false""")
    List<ProductGroupItem.Parent> getParentItems(@Param("ids") List<Long> idsToExclude);
}
