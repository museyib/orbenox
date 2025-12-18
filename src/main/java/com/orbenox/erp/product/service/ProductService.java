package com.orbenox.erp.product.service;

import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.ProductDto;
import com.orbenox.erp.product.mapper.ProductMapper;
import com.orbenox.erp.product.repository.ProductRepository;
import com.orbenox.erp.product.projection.ProductItem;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final LocalizationService i18n;

    public List<ProductItem> findAll() {
        return productRepository.getAllItems();
    }

    public ProductDto findById(Long id) {
        return productMapper.toDto(productRepository.findByIdAndDeletedFalse(id));
    }

    public ProductDto create(ProductDto dto) {
        return productMapper.toDto(productRepository.save(productMapper.toEntity(dto)));
    }

    public ProductDto update(Long id, ProductDto dto) {
        com.orbenox.erp.product.entity.Product entity = productRepository.findByIdAndDeletedFalse(id);
        productMapper.updateEntityFromDto(dto, entity);
        return productMapper.toDto(productRepository.save(entity));
    }

    @Transactional
    public void softDelete(Long id) {
        com.orbenox.erp.product.entity.Product entity = productRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
        com.orbenox.erp.product.entity.Product saved = productRepository.save(entity);
        if (!saved.isDeleted()) {
            throw new IllegalStateException(i18n.msg("error.internal"));
        }
    }
}
