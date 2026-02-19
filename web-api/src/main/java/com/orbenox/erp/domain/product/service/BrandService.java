package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.BrandCreateDto;
import com.orbenox.erp.domain.product.dto.BrandUpdateDto;
import com.orbenox.erp.domain.product.entity.Brand;
import com.orbenox.erp.domain.product.mapper.BrandMapper;
import com.orbenox.erp.domain.product.projection.BrandItem;
import com.orbenox.erp.domain.product.repository.BrandRepository;
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
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Cacheable(BRANDS)
    public Slice<BrandItem> getAllItems(int page, int size, String search) {
        if (isBlank(search))
            return brandRepository.getAllItems(PageRequest.of(page, size));
        return brandRepository.getItemsSearched(PageRequest.of(page, size), search);
    }

    public BrandItem getItemById(Long id) {
        return brandRepository.getItemById(id);
    }

    @CacheEvict(value = {BRANDS, LOOKUPS}, allEntries = true)
    public BrandItem create(BrandCreateDto dto) {
        Brand brand = brandRepository.save(brandMapper.toEntity(dto));
        return brandRepository.getItemById(brand.getId());
    }

    @CacheEvict(value = {BRANDS, LOOKUPS}, allEntries = true)
    @Transactional
    public BrandItem update(Long id, BrandUpdateDto dto) {
        Brand entity = brandRepository.findByIdAndDeletedFalse(id);
        brandMapper.updateEntityFromDto(dto, entity);
        return brandRepository.getItemById(id);
    }

    @CacheEvict(value = {BRANDS, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        Brand entity = brandRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}




