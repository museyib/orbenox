package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.entity.Product;
import com.orbenox.erp.product.summary.ProductSummary;
import com.orbenox.erp.product.summary.UnitSummary;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnitSummaryMapper {
//    UnitSummary toDto(Product entity);
//    Product toEntity(UnitSummary dto);
//    List<UnitSummary> toDtoList(List<Product> entityList);
//    void updateEntityFromDto(UnitSummary dto, @MappingTarget Product entity);
}
