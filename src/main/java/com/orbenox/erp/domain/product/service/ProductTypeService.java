package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductTypeDto;
import com.orbenox.erp.domain.product.entity.ProductType;
import com.orbenox.erp.domain.product.mapper.ProductTypeMapper;
import com.orbenox.erp.domain.product.projection.ProductTypeItem;
import com.orbenox.erp.domain.product.repository.ProductTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductTypeService {
    private final ProductTypeRepository productTypeRepository;
    private final ProductTypeMapper productTypeMapper;

    public List<ProductTypeItem> getAllItems() {
        return productTypeRepository.getAllItems();
    }

    public ProductTypeItem getItemById(Long id) {
        return productTypeRepository.getItemById(id);
    }

    public ProductTypeItem create(ProductTypeDto dto) {
        ProductType productType = productTypeRepository.save(productTypeMapper.toEntity(dto));
        return productTypeRepository.getItemById(productType.getId());
    }

    @Transactional
    public ProductTypeItem update(Long id, ProductTypeDto dto) {
        ProductType entity = productTypeRepository.findByIdAndDeletedFalse(id);
        productTypeMapper.updateEntityFromDto(dto, entity);
        return productTypeRepository.getItemById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        ProductType entity = productTypeRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
