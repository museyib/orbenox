package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductCategoryCreateDto;
import com.orbenox.erp.domain.product.dto.ProductCategoryDto;
import com.orbenox.erp.domain.product.entity.ProductCategory;
import com.orbenox.erp.domain.product.mapper.ProductCategoryMapper;
import com.orbenox.erp.domain.product.projection.ProductCategoryItem;
import com.orbenox.erp.domain.product.repository.ProductCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductCategoryMapper productCategoryMapper;

    @Cacheable("productCategories")
    public List<ProductCategoryItem> getAllItems() {
        return productCategoryRepository.getAllItems();
    }

    public ProductCategoryItem getItemById(Long id) {
        return productCategoryRepository.getItemById(id);
    }

    @CacheEvict(value = {"productCategories", "lookups"}, allEntries = true)
    public ProductCategoryItem create(ProductCategoryCreateDto dto) {
        ProductCategory productCategory = productCategoryRepository.save(productCategoryMapper.toEntity(dto));
        return productCategoryRepository.getItemById(productCategory.getId());
    }

    @CacheEvict(value = {"productCategories", "lookups"}, allEntries = true)
    @Transactional
    public ProductCategoryItem update(Long id, ProductCategoryDto dto) {
        ProductCategory entity = productCategoryRepository.findByIdAndDeletedFalse(id);
        productCategoryMapper.updateEntityFromDto(dto, entity);
        return productCategoryRepository.getItemById(id);
    }

    @CacheEvict(value = {"productCategories", "lookups"}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        ProductCategory entity = productCategoryRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
