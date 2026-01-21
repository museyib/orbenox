package com.orbenox.erp.transaction;

import com.orbenox.erp.domain.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class StockContext {
    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Document document;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Warehouse sourceWarehouse;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Warehouse targetWarehouse;
}
