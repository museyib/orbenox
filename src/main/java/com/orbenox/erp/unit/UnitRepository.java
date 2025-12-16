package com.orbenox.erp.unit;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    @EntityGraph(attributePaths = "unitDimension")
    Unit findByIdAndDeletedFalse(Long id);

    @EntityGraph(attributePaths = "unitDimension")
    List<Unit> findAllByUnitDimensionIdAndDeletedFalseOrderByIdAsc(Long unitDimensionId);

    @EntityGraph(attributePaths = "unitDimension")
    List<Unit> findAllByDeletedFalseOrderByIdAsc();
}
