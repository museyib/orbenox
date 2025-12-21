package com.orbenox.erp.product.service;

import com.orbenox.erp.product.dto.ProductCategoryDto;
import com.orbenox.erp.product.entity.ProductCategory;
import com.orbenox.erp.product.mapper.ProductCategoryMapper;
import com.orbenox.erp.product.projection.ProductCategoryItem;
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

    public List<ProductCategoryItem> getAllItems() {
        return productCategoryRepository.getAllItems();
    }

    public ProductCategoryItem getItemById(Long id) {
        return productCategoryRepository.getItemById(id);
    }

    public ProductCategoryItem create(ProductCategoryDto dto) {
        ProductCategory productCategory = productCategoryRepository.save(productCategoryMapper.toEntity(dto));
        return productCategoryRepository.getItemById(productCategory.getId());
    }

    @Transactional
    public ProductCategoryItem update(Long id, ProductCategoryDto dto) {
        ProductCategory entity = productCategoryRepository.findByIdAndDeletedFalse(id);
        productCategoryMapper.updateEntityFromDto(dto, entity);
        return productCategoryRepository.getItemById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        ProductCategory entity = productCategoryRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
