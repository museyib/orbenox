package com.orbenox.erp.domain.price;

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
import static com.orbenox.erp.common.Utilities.isBlank;

@Service
@RequiredArgsConstructor
public class PriceListService {
    private final PriceListRepository priceListRepository;
    private final PriceListMapper priceListMapper;

    @Cacheable(PRICE_LISTS_ALL)
    public Slice<PriceListItem> getAllItems(int page, int size, String search) {
        if (isBlank(search))
            return priceListRepository.getAllItems(PageRequest.of(page, size));
        return priceListRepository.getItemsSearched(PageRequest.of(page, size), search);
    }

    @Cacheable(PRICE_LISTS_EXCLUDED)
    public List<PriceListItem.PriceListParent> findAllExcluded(Long idToExclude) {
        List<PriceListItem> items = priceListRepository.getAllItems(PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        Map<Long, List<PriceListItem>> childrenMap = items.stream()
                .collect(Collectors.groupingBy(
                        priceListItem -> priceListItem.getParent() == null
                                ? priceListItem.getId()
                                : priceListItem.getParent().getId(),
                        Collectors.toList()));
        Set<Long> excludedIds = new HashSet<>();
        Deque<Long> stack = new ArrayDeque<>();
        stack.push(idToExclude);
        while (!stack.isEmpty()) {
            Long cur = stack.pop();
            excludedIds.add(cur);
            List<PriceListItem> children = childrenMap.get(cur);
            if (children != null) {
                for (PriceListItem group : children) {
                    if (!excludedIds.contains(group.getId())) {
                        stack.push(group.getId());
                    }
                }
            }
        }
        List<Long> allowedIds = items.stream().map(PriceListItem::getId)
                .filter(id -> !excludedIds.contains(id)).toList();
        return priceListRepository.getParentPriceListItems(allowedIds);
    }

    public PriceListItem getItemById(Long id) {
        return priceListRepository.getItemById(id);
    }

    @CacheEvict(value = {
            PRICE_LISTS_ALL,
            PRICE_LISTS_EXCLUDED,
            LOOKUPS}, allEntries = true)
    public PriceListItem create(PriceListCreateDto dto) {
        PriceList priceList = priceListRepository.save(priceListMapper.toEntity(dto));
        return priceListRepository.getItemById(priceList.getId());
    }

    @CacheEvict(value = {
            PRICE_LISTS_ALL,
            PRICE_LISTS_EXCLUDED,
            LOOKUPS}, allEntries = true)
    @Transactional
    public PriceListItem update(Long id, PriceListUpdateDto dto) {
        PriceList entity = priceListRepository.findByIdAndDeletedFalse(id);
        priceListMapper.updateEntityFromDto(dto, entity);
        return priceListRepository.getItemById(id);
    }

    @CacheEvict(value = {
            PRICE_LISTS_ALL,
            PRICE_LISTS_EXCLUDED,
            LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        PriceList entity = priceListRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}




