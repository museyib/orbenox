package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductTypeCreateDto;
import com.orbenox.erp.domain.product.dto.ProductTypeUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductType;
import com.orbenox.erp.domain.product.mapper.ProductTypeMapper;
import com.orbenox.erp.domain.product.projection.ProductTypeItem;
import com.orbenox.erp.domain.product.repository.ProductTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.orbenox.erp.config.CacheConfig.CacheNames.LOOKUPS;
import static com.orbenox.erp.config.CacheConfig.CacheNames.PRODUCT_TYPES;

@Service
@RequiredArgsConstructor
public class ProductTypeService {
    private final ProductTypeRepository productTypeRepository;
    private final ProductTypeMapper productTypeMapper;

    @Cacheable(PRODUCT_TYPES)
    public List<ProductTypeItem> getAllItems() {
        return productTypeRepository.getAllItems();
    }

    public ProductTypeItem getItemById(Long id) {
        return productTypeRepository.getItemById(id);
    }

    @CacheEvict(value = {PRODUCT_TYPES, LOOKUPS}, allEntries = true)
    public ProductTypeItem create(ProductTypeCreateDto dto) {
        ProductType productType = productTypeRepository.save(productTypeMapper.toEntity(dto));
        return productTypeRepository.getItemById(productType.getId());
    }

    @CacheEvict(value = {PRODUCT_TYPES, LOOKUPS}, allEntries = true)
    @Transactional
    public ProductTypeItem update(Long id, ProductTypeUpdateDto dto) {
        ProductType entity = productTypeRepository.findByIdAndDeletedFalse(id);
        productTypeMapper.updateEntityFromDto(dto, entity);
        return productTypeRepository.getItemById(id);
    }

    @CacheEvict(value = {PRODUCT_TYPES, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        ProductType entity = productTypeRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}

