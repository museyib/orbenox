package com.orbenox.erp.domain.price;

import com.orbenox.erp.domain.currency.Currency;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public abstract class PriceListMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Mapping(target = "currency", source = "currencyId")
    @Mapping(target = "parent", source = "parentId")
    abstract PriceList toEntity(PriceListCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "currency", source = "currencyId")
    @Mapping(target = "parent", source = "parentId")
    abstract void updateEntityFromDto(PriceListUpdateDto dto, @MappingTarget PriceList entity);

    Currency mapToCurrency(Long value) {
        return entityManager.getReference(Currency.class, value);
    }

    PriceList mapToPriceList(Long value) {
        return value == 0 ? null : entityManager.getReference(PriceList.class, value);
    }
}
