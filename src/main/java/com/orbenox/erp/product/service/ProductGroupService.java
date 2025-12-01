package com.orbenox.erp.product.service;

import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.ProductGroupDto;
import com.orbenox.erp.product.entity.ProductGroup;
import com.orbenox.erp.product.mapper.ProductGroupMapper;
import com.orbenox.erp.product.repository.ProductGroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductGroupService {
    private final ProductGroupRepository productGroupRepository;
    private final ProductGroupMapper productGroupMapper;
    private final LocalizationService i18n;

    public List<ProductGroupDto> findAll() {
        return productGroupMapper.toDtoList(productGroupRepository.findAllByDeletedFalse());
    }

    public List<ProductGroupDto> findAllExcluded(Long id) {
        List<ProductGroup> groups = productGroupRepository.findAllByDeletedFalse();
        ProductGroup productGroup = productGroupRepository.findByIdAndDeletedFalse(id);
        List<ProductGroup> excluded = productGroupRepository.findAllByDeletedFalseAndParentId(id);
        excluded.add(productGroup);
        groups.removeAll(excluded);
        return productGroupMapper.toDtoList(groups);
    }

    public ProductGroupDto findById(Long id) {
        return productGroupMapper.toDto(productGroupRepository.findByIdAndDeletedFalse(id));
    }

    public ProductGroupDto create(ProductGroupDto dto) {
        System.out.println(dto);
        return productGroupMapper.toDto(productGroupRepository.save(productGroupMapper.toEntity(dto)));
    }

    public ProductGroupDto update(Long id, ProductGroupDto dto) {
        ProductGroup entity = productGroupRepository.findByIdAndDeletedFalse(id);
        productGroupMapper.updateEntityFromDto(dto, entity);
        return productGroupMapper.toDto(productGroupRepository.save(entity));
    }

    @Transactional
    public void softDelete(Long id) {
        ProductGroup entity = productGroupRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
        ProductGroup saved = productGroupRepository.save(entity);
        if (!saved.isDeleted()) {
            throw new IllegalStateException(i18n.msg("error.internal"));
        }
    }
}
