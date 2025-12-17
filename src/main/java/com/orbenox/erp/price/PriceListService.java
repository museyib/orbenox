package com.orbenox.erp.price;

import com.orbenox.erp.localization.LocalizationService;
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
    private final LocalizationService i18n;

    public List<PriceListItem> findAll() {
        return priceListRepository.getAllItems();
    }
    
    public List<PriceListItem.PriceListParent> findAllExcluded(Long idToExclude) {
        List<PriceListItem> summaries = priceListRepository.getAllItems();
        Map<Long, List<PriceListItem>> childrenMap = summaries.stream()
                .collect(Collectors.groupingBy(priceListItem -> {
                    if (priceListItem.getParent() == null)
                        return priceListItem.getId();
                    return priceListItem.getParent().getId();
                    }, Collectors.toList()));
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
        List<Long> allowedIds = summaries.stream().map(PriceListItem::getId)
                .filter(id -> !excludedIds.contains(id)).toList();
        return priceListRepository.getParentPriceListItems(allowedIds);
    }

    public PriceListItem findById(Long id) {
        return priceListRepository.getItemById(id);
    }

    public PriceListDto create(PriceListDto dto) {
        return priceListMapper.toDto(priceListRepository.save(priceListMapper.toEntity(dto)));
    }

    public PriceListDto update(Long id, PriceListDto dto) {
        PriceList entity = priceListRepository.findByIdAndDeletedFalse(id);
        priceListMapper.updateEntityFromDto(dto, entity);
        return priceListMapper.toDto(priceListRepository.save(entity));
    }

    @Transactional
    public void softDelete(Long id) {
        PriceList entity = priceListRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
        PriceList saved = priceListRepository.save(entity);
        if (!saved.isDeleted()) {
            throw new IllegalStateException(i18n.msg("error.internal"));
        }
    }
}
