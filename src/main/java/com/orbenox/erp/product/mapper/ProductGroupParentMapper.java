package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.ProductGroupDto;
import com.orbenox.erp.product.entity.ProductGroup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ProductGroupParentMapper {

    @PersistenceContext
    protected EntityManager em;

    ProductGroup toEntity(ProductGroupDto dto) {
        if (dto == null || dto.id() == null) return null;
        return em.getReference(ProductGroup.class, dto.id());
    }
}
