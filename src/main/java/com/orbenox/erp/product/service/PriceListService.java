package com.orbenox.erp.product.service;

import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.PriceListDto;
import com.orbenox.erp.product.dto.PriceSummary;
import com.orbenox.erp.product.entity.PriceList;
import com.orbenox.erp.product.mapper.PriceListMapper;
import com.orbenox.erp.product.repository.PriceListRepository;
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
        List<PriceSummary> summaries = priceListRepository.findAllSummaries();
        Map<Long, List<PriceSummary>> childrenMap = summaries.stream()
                .collect(Collectors.groupingBy(PriceSummary::getParentId, Collectors.toList()));
        Set<Long> excludedIds = new HashSet<>();
        Deque<Long> stack = new ArrayDeque<>();
        stack.push(idToExclude);
        while (!stack.isEmpty()) {
            Long cur = stack.pop();
            excludedIds.add(cur);
            List<PriceSummary> children = childrenMap.get(cur);
            if (children != null) {
                for (PriceSummary group : children) {
                    if (!excludedIds.contains(group.getId())) {
                        stack.push(group.getId());
                    }
                }
            }
        }
        List<Long> allowedIds = summaries.stream().map(PriceSummary::getId)
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
