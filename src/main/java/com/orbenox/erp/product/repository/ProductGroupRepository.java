package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.dto.ProductGroupSummary;
import com.orbenox.erp.product.entity.ProductGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductGroupRepository extends JpaRepository<ProductGroup, Long> {
    List<ProductGroup> findAllByDeletedFalseOrderByIdAsc();

    ProductGroup findByIdAndDeletedFalse(Long id);

    @Query("""
        select g.id as id, g.name as name,
                coalesce(g.parent.id, 0) as parentId from ProductGroup g
        where g.deleted = false
        """)
    List<ProductGroupSummary> findAllSummaries();
}
