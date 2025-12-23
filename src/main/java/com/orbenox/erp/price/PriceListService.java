package com.orbenox.erp.price;

import com.orbenox.erp.currency.Currency;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceListService {
    private final PriceListRepository priceListRepository;
    private final PriceListMapper priceListMapper;
    private final EntityManager em;

    public List<PriceListItem> getAllItems() {
        return priceListRepository.getAllItems();
    }

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

    public PriceListItem create(PriceListDto dto) {
        PriceList priceList = priceListRepository.save(priceListMapper.toEntity(dto));
        return priceListRepository.getItemById(priceList.getId());
    }

    @Transactional
    public PriceListItem update(Long id, PriceListDto dto) {
        PriceList entity = priceListRepository.findByIdAndDeletedFalse(id);
        priceListMapper.updateEntityFromDto(dto, entity);
        Currency currency = em.getReference(Currency.class, dto.currency().id());
        entity.setCurrency(currency);
        return priceListRepository.getItemById(dto.id());
    }

    @Transactional
    public void softDelete(Long id) {
        PriceList entity = priceListRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
