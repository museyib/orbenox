package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductClassCreateDto;
import com.orbenox.erp.domain.product.dto.ProductClassUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductClass;
import com.orbenox.erp.domain.product.mapper.ProductClassMapper;
import com.orbenox.erp.domain.product.projection.ProductClassItem;
import com.orbenox.erp.domain.product.repository.ProductClassRepository;
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
public class ProductClassService {
    private final ProductClassRepository productClassRepository;
    private final ProductClassMapper productClassMapper;

    @Cacheable(PRODUCT_CLASSES)
    public Slice<ProductClassItem> getAllItems(int page, int size, String search) {
        if (isBlank(search))
            return productClassRepository.getAllItems(PageRequest.of(page, size));
        return productClassRepository.getItemsSearched(PageRequest.of(page, size), search);
    }

    public ProductClassItem getItemById(Long id) {
        return productClassRepository.getItemById(id);
    }

    @CacheEvict(value = {PRODUCT_CLASSES, LOOKUPS}, allEntries = true)
    public ProductClassItem create(ProductClassCreateDto dto) {
        ProductClass productClass = productClassRepository.save(productClassMapper.toEntity(dto));
        return productClassRepository.getItemById(productClass.getId());
    }

    @CacheEvict(value = {PRODUCT_CLASSES, LOOKUPS}, allEntries = true)
    @Transactional
    public ProductClassItem update(Long id, ProductClassUpdateDto dto) {
        ProductClass entity = productClassRepository.findByIdAndDeletedFalse(id);
        productClassMapper.updateEntityFromDto(dto, entity);
        return productClassRepository.getItemById(id);
    }

    @CacheEvict(value = {PRODUCT_CLASSES, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        ProductClass entity = productClassRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}




