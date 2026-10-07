package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.price.PriceList;
import com.orbenox.erp.domain.product.dto.ProductPriceCreateDto;
import com.orbenox.erp.domain.product.dto.ProductPriceUpdateDto;
import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.entity.ProductPrice;
import com.orbenox.erp.domain.unit.Unit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public abstract class ProductPriceMapper {
    @PersistenceContext
    private EntityManager entityManager;

    @Mapping(target = "product", source = "productId")
    @Mapping(target = "priceList", source = "priceListId")
    @Mapping(target = "unit", source = "unitId")
    public abstract ProductPrice toEntity(ProductPriceCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "product", source = "productId")
    @Mapping(target = "priceList", source = "priceListId")
    @Mapping(target = "unit", source = "unitId")
    public abstract void updateEntityFromDto(ProductPriceUpdateDto dto, @MappingTarget ProductPrice entity);

    Product mapToProduct(Long value) {
        return value == 0 ? null : entityManager.getReference(Product.class, value);
    }

    PriceList mapToPriceList(Long value) {
        return value == 0 ? null : entityManager.getReference(PriceList.class, value);
    }

    Unit mapToUnit(Long value) {
        return value == 0 ? null : entityManager.getReference(Unit.class, value);
    }
}
