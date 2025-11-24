package com.orbenox.erp.product.mapper;

import com.orbenox.erp.product.dto.CategoryDto;
import com.orbenox.erp.product.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category entity);
    Category toEntity(CategoryDto dto);
    List<CategoryDto> toDtoList(List<Category> entityList);
    void updateEntityFromDto(CategoryDto dto, @MappingTarget Category entity);
}
