package com.orbenox.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class StockContext {
    @Id
    @Column(name = "document_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Document document;

    @Column(name = "source_warehouse_id")
    private Long sourceWarehouseId;

    @Column(name = "target_warehouse_id")
    private Long targetWarehouseId;
}
