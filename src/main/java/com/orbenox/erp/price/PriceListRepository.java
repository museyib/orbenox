package com.orbenox.erp.price;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PriceListRepository extends JpaRepository<PriceList, Long> {
    List<PriceList> findAllByDeletedFalseOrderByIdAsc();

    PriceList findByIdAndDeletedFalse(Long id);

    @Query("""
            SELECT p.id AS id,
                p.code AS code,
                p.name AS name,
                coalesce(p.parent.id, 0) AS parentId
            FROM PriceList p WHERE p.deleted = false""")
    List<PriceListSummary> findAllSummaries();
}
