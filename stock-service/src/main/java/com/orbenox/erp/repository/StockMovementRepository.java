package com.orbenox.erp.repository;

import com.orbenox.erp.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByDocumentId(Long documentId);

    int countByDocumentId(Long documentId);
}
