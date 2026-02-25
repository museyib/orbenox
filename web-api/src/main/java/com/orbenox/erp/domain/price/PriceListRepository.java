package com.orbenox.erp.domain.price;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PriceListRepository extends JpaRepository<PriceList, Long> {
    PriceList findByIdAndDeletedFalse(Long id);

    @Query("""
            SELECT p.id as id,
                p.code as code,
                p.name as name,
                p.factorToParent as factorToParent,
                p.enabled as enabled,
                p.roundLength as roundLength,
                c as currency,
                pp as parent
            FROM PriceList p
            LEFT JOIN p.parent pp
            LEFT JOIN p.currency c
            WHERE p.deleted = false
            ORDER BY p.id""")
    Slice<PriceListItem> getAllItems(Pageable pageable);

    @Query("""
            SELECT p.id as id,
                p.code as code,
                p.name as name,
                p.factorToParent as factorToParent,
                p.enabled as enabled,
                p.roundLength as roundLength,
                c as currency,
                pp as parent
            FROM PriceList p
            LEFT JOIN p.parent pp
            LEFT JOIN p.currency c
            WHERE p.deleted = false
            ORDER BY p.id""")
    List<PriceListItem> getAllItems();

@Query("""
            SELECT p.id as id,
                p.code as code,
                p.name as name,
                p.factorToParent as factorToParent,
                p.enabled as enabled,
                p.roundLength as roundLength,
                c as currency,
                pp as parent
            FROM PriceList p
            LEFT JOIN p.parent pp
            LEFT JOIN p.currency c
            WHERE  p.deleted = false
                    AND (LOWER(p.code) LIKE %:search% OR LOWER(p.name) LIKE %:search%)
            ORDER BY p.id""")
    Slice<PriceListItem> getItemsSearched(Pageable pageable, @Param("search") String search);

    @Query("""
            SELECT p.id as id,
                p.code as code,
                p.name as name,
                p.factorToParent as factorToParent,
                p.roundLength as roundLength
            FROM PriceList p
            WHERE p.deleted = false AND p.enabled = true
            ORDER BY p.id""")
    List<SimplePriceListItem> getEnabledItems();

    @Query("""
            SELECT p.id as id,
                p.code as code,
                p.name as name,
                p.factorToParent as factorToParent,
                p.enabled as enabled,
                p.roundLength as roundLength,
                c as currency,
                pp as parent
            FROM PriceList p
            LEFT JOIN p.parent pp
            LEFT JOIN p.currency c
            WHERE p.id = :id AND p.deleted = false
            """)
    PriceListItem getItemById(@Param("id") Long id);

    @Query("""
            SELECT p.id as id,
                p.code as code,
                p.name as name,
                p.enabled as enabled,
                p.roundLength as roundLength
            FROM PriceList p
            WHERE p.id IN(:ids) AND p.deleted = false""")
    List<PriceListItem.PriceListParent> getParentPriceListItems(@Param("ids") List<Long> idsToExclude);
}

