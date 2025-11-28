package com.orbenox.erp.product.service;

import com.orbenox.erp.product.entity.Brand;
import com.orbenox.erp.product.dto.BrandDto;
import com.orbenox.erp.product.mapper.BrandMapper;
import com.orbenox.erp.product.repository.BrandRepository;
import com.orbenox.erp.localization.LocalizationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository currencyRepository;
    private final BrandMapper currencyMapper;
    private final LocalizationService i18n;

    public List<BrandDto> findAll() {
        return currencyMapper.toDtoList(currencyRepository.findAllByDeletedFalse());
    }

    public BrandDto findById(Long id) {
        return currencyMapper.toDto(currencyRepository.findByIdAndDeletedFalse(id));
    }

    public BrandDto create(BrandDto dto) {
        return currencyMapper.toDto(currencyRepository.save(currencyMapper.toEntity(dto)));
    }

    public BrandDto update(Long id, BrandDto dto) {
        Brand entity = currencyRepository.findByIdAndDeletedFalse(id);
        currencyMapper.updateEntityFromDto(dto, entity);
        return currencyMapper.toDto(currencyRepository.save(entity));
    }

    @Transactional
    public void softDelete(Long id) {
        Brand entity = currencyRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
        Brand saved = currencyRepository.save(entity);
        if (!saved.isDeleted()) {
            throw new IllegalStateException(i18n.msg("error.internal"));
        }
    }
}
