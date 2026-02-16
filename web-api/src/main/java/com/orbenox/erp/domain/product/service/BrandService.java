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
import org.springframework.stereotype.Service;

import java.util.List;

import static com.orbenox.erp.config.CacheConfig.CacheNames.BRANDS;
import static com.orbenox.erp.config.CacheConfig.CacheNames.LOOKUPS;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository currencyRepository;
    private final BrandMapper currencyMapper;

    @Cacheable(BRANDS)
    public List<BrandItem> getAllItems() {
        return currencyRepository.getAllItems();
    }

    public BrandItem getItemById(Long id) {
        return currencyRepository.getItemById(id);
    }

    @CacheEvict(value = {BRANDS, LOOKUPS}, allEntries = true)
    public BrandItem create(BrandCreateDto dto) {
        Brand brand = currencyRepository.save(currencyMapper.toEntity(dto));
        return currencyRepository.getItemById(brand.getId());
    }

    @CacheEvict(value = {BRANDS, LOOKUPS}, allEntries = true)
    @Transactional
    public BrandItem update(Long id, BrandUpdateDto dto) {
        Brand entity = currencyRepository.findByIdAndDeletedFalse(id);
        currencyMapper.updateEntityFromDto(dto, entity);
        return currencyRepository.getItemById(id);
    }

    @CacheEvict(value = {BRANDS, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        Brand entity = currencyRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}

