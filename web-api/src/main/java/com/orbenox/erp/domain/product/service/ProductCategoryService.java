package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductCategoryCreateDto;
import com.orbenox.erp.domain.product.dto.ProductCategoryUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductCategory;
import com.orbenox.erp.domain.product.mapper.ProductCategoryMapper;
import com.orbenox.erp.domain.product.projection.ProductCategoryItem;
import com.orbenox.erp.domain.product.repository.ProductCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;


import static com.orbenox.erp.config.CacheConfig.CacheNames.*;
import static com.orbenox.erp.common.Utilities.isBlank;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductCategoryMapper productCategoryMapper;

    @Cacheable(PRODUCT_CATEGORIES)
    public Slice<ProductCategoryItem> getAllItems(int page, int size, String search) {
        if (isBlank(search))
            return productCategoryRepository.getAllItems(PageRequest.of(page, size));
        return productCategoryRepository.getItemsSearched(PageRequest.of(page, size), search);
    }

    public ProductCategoryItem getItemById(Long id) {
        return productCategoryRepository.getItemById(id);
    }

    @CacheEvict(value = {PRODUCT_CATEGORIES, LOOKUPS}, allEntries = true)
    public ProductCategoryItem create(ProductCategoryCreateDto dto) {
        ProductCategory productCategory = productCategoryRepository.save(productCategoryMapper.toEntity(dto));
        return productCategoryRepository.getItemById(productCategory.getId());
    }

    @CacheEvict(value = {PRODUCT_CATEGORIES, LOOKUPS}, allEntries = true)
    @Transactional
    public ProductCategoryItem update(Long id, ProductCategoryUpdateDto dto) {
        ProductCategory entity = productCategoryRepository.findByIdAndDeletedFalse(id);
        productCategoryMapper.updateEntityFromDto(dto, entity);
        return productCategoryRepository.getItemById(id);
    }

    @CacheEvict(value = {PRODUCT_CATEGORIES, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        ProductCategory entity = productCategoryRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}




