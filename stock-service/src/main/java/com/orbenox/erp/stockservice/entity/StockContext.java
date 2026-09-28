package com.orbenox.erp.stockservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class StockContext {
    @Id
    private Long id;

    @Column(name = "document_id")
    private Long documentId;

    @Column(name = "source_warehouse_id")
    private Long sourceWarehouseId;

    @Column(name = "target_warehouse_id")
    private Long targetWarehouseId;
}
