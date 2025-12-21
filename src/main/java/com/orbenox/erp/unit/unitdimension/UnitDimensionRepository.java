package com.orbenox.erp.unit.unitdimension;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UnitDimensionRepository extends JpaRepository<UnitDimension, Long> {
    @Query("""
        SELECT d.id as id,
               d.code as code,
               d.name as name,
               d.enabled as enabled
        FROM UnitDimension d
        WHERE d.deleted = false""")
    List<UnitDimensionItem> getAllItems();
}