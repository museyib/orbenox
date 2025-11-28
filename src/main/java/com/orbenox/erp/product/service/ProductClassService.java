package com.orbenox.erp.product.service;

import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.ProductClassDto;
import com.orbenox.erp.product.entity.ProductClass;
import com.orbenox.erp.product.mapper.ProductClassMapper;
import com.orbenox.erp.product.repository.ProductClassRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductClassService {
    private final ProductClassRepository productClassRepository;
    private final ProductClassMapper productClassMapper;
    private final LocalizationService i18n;

    public List<ProductClassDto> findAll() {
        return productClassMapper.toDtoList(productClassRepository.findAllByDeletedFalse());
    }

    public ProductClassDto findById(Long id) {
        return productClassMapper.toDto(productClassRepository.findByIdAndDeletedFalse(id));
    }

    public ProductClassDto create(ProductClassDto dto) {
        return productClassMapper.toDto(productClassRepository.save(productClassMapper.toEntity(dto)));
    }

    public ProductClassDto update(Long id, ProductClassDto dto) {
        ProductClass entity = productClassRepository.findByIdAndDeletedFalse(id);
        productClassMapper.updateEntityFromDto(dto, entity);
        return productClassMapper.toDto(productClassRepository.save(entity));
    }

    @Transactional
    public void softDelete(Long id) {
        ProductClass entity = productClassRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
        ProductClass saved = productClassRepository.save(entity);
        if (!saved.isDeleted()) {
            throw new IllegalStateException(i18n.msg("error.internal"));
        }
    }
}
