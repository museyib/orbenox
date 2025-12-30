package com.orbenox.erp.domain.price;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class PriceListParentMapper {

    @PersistenceContext
    protected EntityManager em;

    PriceList toEntity(PriceListDto.Parent dto) {
        if (dto == null || dto.id() == null) return null;
        return em.getReference(PriceList.class, dto.id());
    }
}
