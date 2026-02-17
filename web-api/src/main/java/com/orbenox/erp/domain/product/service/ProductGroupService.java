package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductGroupCreateDto;
import com.orbenox.erp.domain.product.dto.ProductGroupUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductGroup;
import com.orbenox.erp.domain.product.mapper.ProductGroupMapper;
import com.orbenox.erp.domain.product.projection.ProductGroupItem;
import com.orbenox.erp.domain.product.projection.SimpleProductGroupItem;
import com.orbenox.erp.domain.product.repository.ProductGroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.orbenox.erp.config.CacheConfig.CacheNames.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class ProductGroupService {
    private final ProductGroupRepository productGroupRepository;
    private final ProductGroupMapper productGroupMapper;

    @Cacheable(PRODUCT_GROUPS_ALL)
    public Slice<ProductGroupItem> getAllItems(int page, int size, String search) {
        if (isEmpty(search))
            return productGroupRepository.getAllItems(PageRequest.of(page, size));
        return productGroupRepository.getItemsSearched(PageRequest.of(page, size), search);
    }

    @Cacheable(PRODUCT_GROUPS_EXCLUDED)
    public List<SimpleProductGroupItem> findAllExcluded(Long idToExclude) {
        List<ProductGroupItem> items = productGroupRepository.getAllItems(PageRequest.of(0, Integer.MAX_VALUE)).getContent();
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

    @CacheEvict(value = {
            PRODUCT_GROUPS_ALL,
            PRODUCT_GROUPS_EXCLUDED,
            LOOKUPS}, allEntries = true)
    public ProductGroupItem create(ProductGroupCreateDto dto) {
        ProductGroup productGroup = productGroupRepository.save(productGroupMapper.toEntity(dto));
        return productGroupRepository.getItemById(productGroup.getId());
    }

    @CacheEvict(value = {
            PRODUCT_GROUPS_ALL,
            PRODUCT_GROUPS_EXCLUDED,
            LOOKUPS}, allEntries = true)
    @Transactional
    public ProductGroupItem update(Long id, ProductGroupUpdateDto dto) {
        ProductGroup entity = productGroupRepository.findByIdAndDeletedFalse(id);
        productGroupMapper.updateEntityFromDto(dto, entity);
        return productGroupRepository.getItemById(id);
    }

    @CacheEvict(value = {
            PRODUCT_GROUPS_ALL,
            PRODUCT_GROUPS_EXCLUDED,
            LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        ProductGroup entity = productGroupRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}




