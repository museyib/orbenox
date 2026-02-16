package com.orbenox.erp.domain.product.entity;

import com.orbenox.erp.common.entity.BaseCardEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class ProductGroup extends BaseCardEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ProductGroup parent;

    @OneToMany(mappedBy = "parent")
    private List<ProductGroup> children;

    private String slug;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productGroup")
    private Set<Product> products;
}