package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.country.Country;
import com.orbenox.erp.domain.product.dto.ProductCreateDto;
import com.orbenox.erp.domain.product.dto.ProductUpdateDto;
import com.orbenox.erp.domain.product.entity.*;
import com.orbenox.erp.domain.unit.Unit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Mapping(target = "brand", source = "brandId")
    @Mapping(target = "productType", source = "productTypeId")
    @Mapping(target = "productClass", source = "productClassId")
    @Mapping(target = "productGroup", source = "productGroupId")
    @Mapping(target = "productCategory", source = "productCategoryId")
    @Mapping(target = "producer", source = "producerId")
    @Mapping(target = "country", source = "countryId")
    @Mapping(target = "defaultUnit", source = "defaultUnitId")
    public abstract Product toEntity(ProductCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "brand", source = "brandId")
    @Mapping(target = "productType", source = "productTypeId")
    @Mapping(target = "productClass", source = "productClassId")
    @Mapping(target = "productGroup", source = "productGroupId")
    @Mapping(target = "productCategory", source = "productCategoryId")
    @Mapping(target = "producer", source = "producerId")
    @Mapping(target = "country", source = "countryId")
    @Mapping(target = "defaultUnit", source = "defaultUnitId")
    public abstract void updateEntityFromDto(ProductUpdateDto dto, @MappingTarget Product entity);

    Brand mapToBrand(Long value) {
        return value == 0 ? null : entityManager.getReference(Brand.class, value);
    }

    ProductType mapToProductType(Long value) {
        return value == 0 ? null : entityManager.getReference(ProductType.class, value);
    }

    ProductClass mapToProductClass(Long value) {
        return value == 0 ? null : entityManager.getReference(ProductClass.class, value);
    }

    ProductGroup mapToProductGroup(Long value) {
        return value == 0 ? null : entityManager.getReference(ProductGroup.class, value);
    }

    ProductCategory mapToProductCategory(Long value) {
        return value == 0 ? null : entityManager.getReference(ProductCategory.class, value);
    }

    Producer mapToProducer(Long value) {
        return value == 0 ? null : entityManager.getReference(Producer.class, value);
    }

    Country mapToCountry(Long value) {
        return value == 0 ? null : entityManager.getReference(Country.class, value);
    }

    Unit mapToUnit(Long value) {
        return value == 0 ? null : entityManager.getReference(Unit.class, value);
    }
}
