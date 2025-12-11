package com.orbenox.erp.product.service;

import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.ProductTypeDto;
import com.orbenox.erp.product.entity.ProductType;
import com.orbenox.erp.product.mapper.ProductTypeMapper;
import com.orbenox.erp.product.repository.ProductTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductTypeService {
    private final ProductTypeRepository productTypeRepository;
    private final ProductTypeMapper productTypeMapper;
    private final LocalizationService i18n;

    public List<ProductTypeDto> findAll() {
        return productTypeMapper.toDtoList(productTypeRepository.findAllByDeletedFalseOrderByIdAsc());
    }

    public ProductTypeDto findById(Long id) {
        return productTypeMapper.toDto(productTypeRepository.findByIdAndDeletedFalse(id));
    }

    public ProductTypeDto create(ProductTypeDto dto) {
        return productTypeMapper.toDto(productTypeRepository.save(productTypeMapper.toEntity(dto)));
    }

    public ProductTypeDto update(Long id, ProductTypeDto dto) {
        ProductType entity = productTypeRepository.findByIdAndDeletedFalse(id);
        productTypeMapper.updateEntityFromDto(dto, entity);
        return productTypeMapper.toDto(productTypeRepository.save(entity));
    }

    @Transactional
    public void softDelete(Long id) {
        ProductType entity = productTypeRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
        ProductType saved = productTypeRepository.save(entity);
        if (!saved.isDeleted()) {
            throw new IllegalStateException(i18n.msg("error.internal"));
        }
    }
}
