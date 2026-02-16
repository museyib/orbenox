package com.orbenox.erp.transaction.entity;

import com.orbenox.erp.domain.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class StockContext {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    private Warehouse sourceWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    private Warehouse targetWarehouse;
}
