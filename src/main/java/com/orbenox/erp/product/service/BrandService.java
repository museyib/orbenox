package com.orbenox.erp.product.service;

import com.orbenox.erp.product.dto.BrandDto;
import com.orbenox.erp.product.entity.Brand;
import com.orbenox.erp.product.mapper.BrandMapper;
import com.orbenox.erp.product.projection.BrandItem;
import com.orbenox.erp.product.repository.BrandRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository currencyRepository;
    private final BrandMapper currencyMapper;

    public List<BrandItem> getAllItems() {
        return currencyRepository.getAllItems();
    }

    public BrandItem getItemById(Long id) {
        return currencyRepository.getItemById(id);
    }

    public BrandItem create(BrandDto dto) {
        Brand brand = currencyRepository.save(currencyMapper.toEntity(dto));
        return currencyRepository.getItemById(brand.getId());
    }

    @Transactional
    public BrandItem update(Long id, BrandDto dto) {
        Brand entity = currencyRepository.findByIdAndDeletedFalse(id);
        currencyMapper.updateEntityFromDto(dto, entity);
        return currencyRepository.getItemById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        Brand entity = currencyRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
