package com.orbenox.erp.transaction.product;

import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.unit.Unit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class ProductTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProductTransactionRoot root;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Unit unit;

    private BigDecimal quantity;

    private BigDecimal price;
}