package com.orbenox.erp.product.service;

import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.ProductCategoryDto;
import com.orbenox.erp.product.entity.ProductCategory;
import com.orbenox.erp.product.mapper.ProductCategoryMapper;
import com.orbenox.erp.product.repository.ProductCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductCategoryMapper productCategoryMapper;
    private final LocalizationService i18n;

    public List<ProductCategoryDto> findAll() {
        return productCategoryMapper.toDtoList(productCategoryRepository.findAllByDeletedFalseOrderByIdAsc());
    }

    public ProductCategoryDto findById(Long id) {
        return productCategoryMapper.toDto(productCategoryRepository.findByIdAndDeletedFalse(id));
    }

    public ProductCategoryDto create(ProductCategoryDto dto) {
        return productCategoryMapper.toDto(productCategoryRepository.save(productCategoryMapper.toEntity(dto)));
    }

    public ProductCategoryDto update(Long id, ProductCategoryDto dto) {
        ProductCategory entity = productCategoryRepository.findByIdAndDeletedFalse(id);
        productCategoryMapper.updateEntityFromDto(dto, entity);
        return productCategoryMapper.toDto(productCategoryRepository.save(entity));
    }

    @Transactional
    public void softDelete(Long id) {
        ProductCategory entity = productCategoryRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
        ProductCategory saved = productCategoryRepository.save(entity);
        if (!saved.isDeleted()) {
            throw new IllegalStateException(i18n.msg("error.internal"));
        }
    }
}
