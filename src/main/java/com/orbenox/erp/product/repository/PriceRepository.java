package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.dto.PriceSummary;
import com.orbenox.erp.product.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Long> {
    List<Price> findAllByDeletedFalse();

    Price findByIdAndDeletedFalse(Long id);

    @Query("""
            select p.id as id, p.code as code, p.name as name, coalesce(p.parent.id, 0) as parentId
                        from Price p where p.deleted = false""")
    List<PriceSummary> findAllSummaries();
}
