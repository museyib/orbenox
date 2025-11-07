package com.orbenox.erp.unitofmeasure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<UnitOfMeasure, Long> {
}
