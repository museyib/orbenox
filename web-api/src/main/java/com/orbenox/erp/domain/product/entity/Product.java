package com.orbenox.erp.domain.product.entity;

import com.orbenox.erp.common.entity.BaseCardEntity;
import com.orbenox.erp.domain.country.Country;
import com.orbenox.erp.domain.unit.Unit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Product extends BaseCardEntity {

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Producer producer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProductType productType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProductGroup productGroup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProductCategory productCategory;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProductClass productClass;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Unit defaultUnit;

    @Column(nullable = false)
    private String defaultBarcode;
}