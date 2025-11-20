package com.orbenox.erp.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ProductPriceId implements Serializable {
    private static final long serialVersionUID = -569652956771335409L;
    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull
    @Column(name = "price_id", nullable = false)
    private Long priceId;

    @NotNull
    @Column(name = "unit_id", nullable = false)
    private Long unitId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductPriceId entity = (ProductPriceId) o;
        return Objects.equals(this.productId, entity.productId) &&
                Objects.equals(this.unitId, entity.unitId) &&
                Objects.equals(this.priceId, entity.priceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, unitId, priceId);
    }

}