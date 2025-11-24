package com.orbenox.erp.unit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    Unit findByIdAndDeleted(Long id, Boolean deleted);

    List<Unit> findAllByUnitDimensionIdAndDeleted(Long unitDimensionId, Boolean deleted);
}
