package com.orbenox.erp.domain.unit;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    @EntityGraph(attributePaths = "unitDimension")
    Unit findByIdAndDeletedFalse(Long id);

    @Query("""
            SELECT u.id as id,
                   u.code as code,
                   u.name as name,
                   u.enabled as enabled,
                   u.base as base,
                   u.factorToBase as factorToBase,
                   u.offsetToBase as offsetToBase,
                   d as unitDimension
            FROM Unit u
            LEFT JOIN u.unitDimension d
            WHERE u.deleted = false
            ORDER BY u.id""")
    Slice<UnitItem> getAllItems(Pageable pageable);

@Query("""
            SELECT u.id as id,
                   u.code as code,
                   u.name as name,
                   u.enabled as enabled,
                   u.base as base,
                   u.factorToBase as factorToBase,
                   u.offsetToBase as offsetToBase,
                   d as unitDimension
            FROM Unit u
            LEFT JOIN u.unitDimension d
            WHERE  u.deleted = false
                    AND (LOWER(u.code) LIKE %:search% OR LOWER(u.name) LIKE %:search%)
            ORDER BY u.id""")
    Slice<UnitItem> getItemsSearched(Pageable pageable, @Param("search") String search);

    @Query("""
            SELECT u.id as id,
                   u.code as code,
                   u.name as name,
                   u.enabled as enabled,
                   u.base as base,
                   u.factorToBase as factorToBase,
                   u.offsetToBase as offsetToBase,
                   d as unitDimension
            FROM Unit u
            LEFT JOIN u.unitDimension d
            WHERE u.unitDimension.id = :unitDimensionId AND u.deleted = false
            ORDER BY u.id""")
    List<UnitItem> getItemsByUnitDimensionId(@Param("unitDimensionId") Long unitDimensionId);

    @Query("""
            SELECT u.id as id,
                   u.code as code,
                   u.name as name,
                   u.enabled as enabled,
                   u.base as base,
                   u.factorToBase as factorToBase,
                   u.offsetToBase as offsetToBase,
                   d as unitDimension
            FROM Unit u
            LEFT JOIN u.unitDimension d
            WHERE u.id = :id AND u.deleted = false
            ORDER BY u.id""")
    UnitItem getItemById(@Param("id") Long id);


    @Query("""
            SELECT u.id as id,
                   u.code as code,
                   u.name as name,
                   u.enabled as enabled
            FROM Unit u
            WHERE u.deleted = false AND u.enabled = true
            ORDER BY u.id""")
    List<SimpleUnitItem> getEnabledItems();
}

