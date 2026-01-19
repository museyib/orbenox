package com.orbenox.erp.transaction.product;

import com.orbenox.erp.common.entity.BaseTransactionEntity;
import com.orbenox.erp.domain.price.PriceList;
import com.orbenox.erp.domain.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class ProductTransactionRoot extends BaseTransactionEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PriceList priceList;

    @OneToMany(mappedBy = "root")
    private List<ProductTransaction> transactions;
}