package com.orbenox.erp.product.entity;

import com.orbenox.erp.common.entity.BaseCardEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
public class Brand extends BaseCardEntity {

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    private String logoUrl;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "brand")
    private Set<Product> products;
}