package com.orbenox.erp.domain.product.mapper;

import com.orbenox.erp.domain.product.dto.ProductWarehouseCreateDto;
import com.orbenox.erp.domain.product.dto.ProductWarehouseUpdateDto;
import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.entity.ProductWarehouse;
import com.orbenox.erp.domain.warehouse.Warehouse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public abstract class ProductWarehouseMapper {
    @PersistenceContext
    private EntityManager entityManager;

    @Mapping(target = "product", source = "productId")
    @Mapping(target = "warehouse", source = "warehouseId")
    public abstract ProductWarehouse toEntity(ProductWarehouseCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "product", source = "productId")
    @Mapping(target = "warehouse", source = "warehouseId")
    public abstract void updateEntityFromDto(ProductWarehouseUpdateDto dto, @MappingTarget ProductWarehouse entity);

    Product mapToProduct(Long value) {
        return value == 0 ? null : entityManager.getReference(Product.class, value);
    }

    Warehouse mapToWarehouse(Long value) {
        return value == 0 ? null : entityManager.getReference(Warehouse.class, value);
    }
}
