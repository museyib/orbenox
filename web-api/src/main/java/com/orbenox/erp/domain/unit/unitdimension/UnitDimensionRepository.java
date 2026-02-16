package com.orbenox.erp.domain.unit.unitdimension;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UnitDimensionRepository extends JpaRepository<UnitDimension, Long> {
    @Query("""
            SELECT d.id as id,
                   d.code as code,
                   d.name as name,
                   d.enabled as enabled
            FROM UnitDimension d
            WHERE d.deleted = false
            order by d.id""")
    List<UnitDimensionItem> getAllItems();

    @Query("""
            SELECT d.id as id,
                   d.code as code,
                   d.name as name,
                   d.enabled as enabled
            FROM UnitDimension d
            WHERE d.deleted = false AND d.enabled = true
            order by d.id""")
    List<UnitDimensionItem> getEnabledItems();

    @Query("""
            SELECT d.id as id,
                   d.code as code,
                   d.name as name,
                   d.enabled as enabled
            FROM UnitDimension d
            WHERE d.id = :id AND d.deleted = false
            """)
    UnitDimensionItem getItemById(@Param("id") Long id);

    UnitDimension findByIdAndDeletedFalse(Long id);
}
