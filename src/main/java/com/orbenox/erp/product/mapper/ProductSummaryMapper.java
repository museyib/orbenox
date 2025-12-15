package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.ProductDto;
import com.orbenox.erp.product.entity.Product;
import com.orbenox.erp.product.summary.ProductSummary;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductSummaryMapper {
//    ProductSummary toDto(Product entity);
//    Product toEntity(ProductSummary dto);
//    List<ProductSummary> toDtoList(List<Product> entityList);
//    void updateEntityFromDto(ProductSummary dto, @MappingTarget Product entity);
}
