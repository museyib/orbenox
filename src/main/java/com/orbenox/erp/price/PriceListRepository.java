package com.orbenox.erp.price;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PriceListRepository extends JpaRepository<PriceList, Long> {
    PriceList findByIdAndDeletedFalse(Long id);

    @Query("""
            SELECT p.id AS id,
                p.code AS code,
                p.name AS name,
                p.factorToParent AS factorToParent,
                p.enabled AS enabled,
                coalesce(p.parent.id, 0L) AS parentId,
                p.currency as currency
            FROM PriceList p WHERE p.deleted = false
            ORDER BY p.id""")
    List<PriceListItem> getAllItems();

    @Query("""
            SELECT p.id AS id,
                p.code AS code,
                p.name AS name,
                p.factorToParent AS factorToParent,
                p.enabled AS enabled,
                coalesce(p.parent.id, 0L) AS parentId,
                p.currency as currency
            FROM PriceList p WHERE p.id = :id AND p.deleted = false""")
    Optional<PriceListItem> getItemById(@Param("id") Long id);
}
