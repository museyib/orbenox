package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.dto.PriceSummary;
import com.orbenox.erp.product.entity.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PriceListRepository extends JpaRepository<PriceList, Long> {
    List<PriceList> findAllByDeletedFalseOrderByIdAsc();

    PriceList findByIdAndDeletedFalse(Long id);

    @Query("""
            select p.id as id, p.code as code, p.name as name, coalesce(p.parent.id, 0) as parentId
                        from PriceList p where p.deleted = false""")
    List<PriceSummary> findAllSummaries();
}
