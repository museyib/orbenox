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

    public List<PriceListDto> findAll() {
        return priceListMapper.toDtoList(priceListRepository.findAllByDeletedFalseOrderByIdAsc());
    }
    
    public List<PriceListDto> findAllExcluded(Long idToExclude) {
        List<PriceListSummary> summaries = priceListRepository.findAllSummaries();
        Map<Long, List<PriceListSummary>> childrenMap = summaries.stream()
                .collect(Collectors.groupingBy(PriceListSummary::getParentId, Collectors.toList()));
        Set<Long> excludedIds = new HashSet<>();
        Deque<Long> stack = new ArrayDeque<>();
        stack.push(idToExclude);
        while (!stack.isEmpty()) {
            Long cur = stack.pop();
            excludedIds.add(cur);
            List<PriceListSummary> children = childrenMap.get(cur);
            if (children != null) {
                for (PriceListSummary group : children) {
                    if (!excludedIds.contains(group.getId())) {
                        stack.push(group.getId());
                    }
                }
            }
        }
        List<Long> allowedIds = summaries.stream().map(PriceListSummary::getId)
                .filter(id -> !excludedIds.contains(id)).toList();
        return priceListMapper.toDtoList(priceListRepository.findAllById(allowedIds));
    }

    public PriceListDto findById(Long id) {
        return priceListMapper.toDto(priceListRepository.findByIdAndDeletedFalse(id));
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
