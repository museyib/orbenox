package com.orbenox.erp.product.entity;

import com.orbenox.erp.common.country.Country;
import com.orbenox.erp.common.entity.BaseCardEntity;
import com.orbenox.erp.unit.Unit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
public class Product extends BaseCardEntity {

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne
    private Brand brand;

    @ManyToOne
    private Producer producer;

    @ManyToOne
    private ProductType productType;

    @ManyToOne
    private ProductGroup productGroup;

    @ManyToOne
    private ProductCategory productCategory;

    @ManyToOne
    private ProductClass productClass;

    @ManyToOne
    private Country country;

    @ManyToOne
    private Unit defaultUnit;

    private String defaultBarcode;

    @ManyToMany
    @JoinTable(
            name = "product_price",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "price_id")
    )
    private Set<ProductPriceList> prices;
}