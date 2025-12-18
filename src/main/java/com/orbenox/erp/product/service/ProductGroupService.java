package com.orbenox.erp.product.service;

import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.ProductGroupDto;
import com.orbenox.erp.product.projection.ProductGroupItem;
import com.orbenox.erp.product.entity.ProductGroup;
import com.orbenox.erp.product.mapper.ProductGroupMapper;
import com.orbenox.erp.product.repository.ProductGroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductGroupService {
    private final ProductGroupRepository productGroupRepository;
    private final ProductGroupMapper productGroupMapper;
    private final LocalizationService i18n;

    public List<ProductGroupDto> findAll() {
        return productGroupMapper.toDtoList(productGroupRepository.findAllByDeletedFalseOrderByIdAsc());
    }

    public List<ProductGroupItem.Parent> findAllExcluded(Long idToExclude) {
        List<ProductGroupItem> items = productGroupRepository.getAllItems();
        Map<Long, List<ProductGroupItem>> childrenMap = items.stream()
                .collect(Collectors.groupingBy(
                        groupItem -> groupItem.getParent() == null
                                ? groupItem.getId()
                                : groupItem.getParent().getId(),
                        Collectors.toList()));
        Set<Long> excludedIds = new HashSet<>();
        Deque<Long> stack = new ArrayDeque<>();
        stack.push(idToExclude);
        while (!stack.isEmpty()) {
            Long cur = stack.pop();
            excludedIds.add(cur);
            List<ProductGroupItem> children = childrenMap.get(cur);
            if (children != null) {
                for (ProductGroupItem group : children) {
                    if (!excludedIds.contains(group.getId())) {
                        stack.push(group.getId());
                    }
                }
            }
        }
        List<Long> allowedIds = items.stream().map(ProductGroupItem::getId)
                .filter(id -> !excludedIds.contains(id)).toList();
        return productGroupRepository.getParentItems(allowedIds);
    }

    public ProductGroupDto findById(Long id) {
        return productGroupMapper.toDto(productGroupRepository.findByIdAndDeletedFalse(id));
    }

    public ProductGroupDto create(ProductGroupDto dto) {
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
