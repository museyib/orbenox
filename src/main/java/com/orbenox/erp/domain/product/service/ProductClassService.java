package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductClassDto;
import com.orbenox.erp.domain.product.entity.ProductClass;
import com.orbenox.erp.domain.product.mapper.ProductClassMapper;
import com.orbenox.erp.domain.product.projection.ProductClassItem;
import com.orbenox.erp.domain.product.repository.ProductClassRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductClassService {
    private final ProductClassRepository productClassRepository;
    private final ProductClassMapper productClassMapper;

    public List<ProductClassItem> getAllItems() {
        return productClassRepository.getAllItems();
    }

    public ProductClassItem getItemById(Long id) {
        return productClassRepository.getItemById(id);
    }

    public ProductClassItem create(ProductClassDto dto) {
        ProductClass productClass = productClassRepository.save(productClassMapper.toEntity(dto));
        return productClassRepository.getItemById(productClass.getId());
    }

    @Transactional
    public ProductClassItem update(Long id, ProductClassDto dto) {
        ProductClass entity = productClassRepository.findByIdAndDeletedFalse(id);
        productClassMapper.updateEntityFromDto(dto, entity);
        return productClassRepository.getItemById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        ProductClass entity = productClassRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
