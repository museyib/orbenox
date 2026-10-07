package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductUnitCreateDto;
import com.orbenox.erp.domain.product.dto.ProductUnitUpdateDto;
import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.entity.ProductUnit;
import com.orbenox.erp.domain.unit.Unit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public abstract class ProductUnitMapper {
    @PersistenceContext
    private EntityManager entityManager;

    @Mapping(target = "product", source = "productId")
    @Mapping(target = "unit", source = "unitId")
    public abstract ProductUnit toEntity(ProductUnitCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "product", source = "productId")
    @Mapping(target = "unit", source = "unitId")
    public abstract void updateEntityFromDto(ProductUnitUpdateDto dto, @MappingTarget ProductUnit entity);

    Product mapToProduct(Long value) {
        return value == 0 ? null : entityManager.getReference(Product.class, value);
    }

    Unit mapToUnit(Long value) {
        return value == 0 ? null : entityManager.getReference(Unit.class, value);
    }
}
