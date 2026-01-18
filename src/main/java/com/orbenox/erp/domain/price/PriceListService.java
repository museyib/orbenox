package com.orbenox.erp.domain.price;

import com.orbenox.erp.domain.currency.Currency;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceListService {
    private final PriceListRepository priceListRepository;
    private final PriceListMapper priceListMapper;
    private final EntityManager em;

    @Cacheable("priceLists.all")
    public List<PriceListItem> getAllItems() {
        return priceListRepository.getAllItems();
    }

    @Cacheable("priceLists.excluded")
    public List<PriceListItem.PriceListParent> findAllExcluded(Long idToExclude) {
        List<PriceListItem> items = priceListRepository.getAllItems();
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
            "priceLists.all",
            "priceLists.excluded",
            "lookups"}, allEntries = true)
    public PriceListItem create(PriceListCreateDto dto) {
        PriceList priceList = priceListRepository.save(priceListMapper.toEntity(dto));
        return priceListRepository.getItemById(priceList.getId());
    }

    @CacheEvict(value = {
            "priceLists.all",
            "priceLists.excluded",
            "lookups"}, allEntries = true)
    @Transactional
    public PriceListItem update(Long id, PriceListDto dto) {
        PriceList entity = priceListRepository.findByIdAndDeletedFalse(id);
        priceListMapper.updateEntityFromDto(dto, entity);
        Currency currency = em.getReference(Currency.class, dto.currency().id());
        entity.setCurrency(currency);
        return priceListRepository.getItemById(id);
    }

    @CacheEvict(value = {
            "priceLists.all",
            "priceLists.excluded",
            "lookups"}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        PriceList entity = priceListRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
