package com.orbenox.erp.transaction.repository;

import com.orbenox.erp.transaction.entity.ProductLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLineRepository extends JpaRepository<ProductLine, Long> {
}
