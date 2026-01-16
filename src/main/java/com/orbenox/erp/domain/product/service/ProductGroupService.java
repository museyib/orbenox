package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductGroupDto;
import com.orbenox.erp.domain.product.entity.ProductGroup;
import com.orbenox.erp.domain.product.mapper.ProductGroupMapper;
import com.orbenox.erp.domain.product.projection.ProductGroupItem;
import com.orbenox.erp.domain.product.projection.SimpleProductGroupItem;
import com.orbenox.erp.domain.product.repository.ProductGroupRepository;
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

    public List<ProductGroupItem> getAllItems() {
        return productGroupRepository.getAllItems();
    }

    public List<SimpleProductGroupItem> findAllExcluded(Long idToExclude) {
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
        return productGroupRepository.getItemsExcluded(allowedIds);
    }

    public ProductGroupItem getItemById(Long id) {
        return productGroupRepository.getItemById(id);
    }

    public ProductGroupItem create(ProductGroupDto dto) {
        ProductGroup productGroup = productGroupRepository.save(productGroupMapper.toEntity(dto));
        return productGroupRepository.getItemById(productGroup.getId());
    }

    @Transactional
    public ProductGroupItem update(Long id, ProductGroupDto dto) {
        ProductGroup entity = productGroupRepository.findByIdAndDeletedFalse(id);
        productGroupMapper.updateEntityFromDto(dto, entity);
        return productGroupRepository.getItemById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        ProductGroup entity = productGroupRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
