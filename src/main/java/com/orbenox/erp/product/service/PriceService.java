package com.orbenox.erp.product.service;

import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.PriceDto;
import com.orbenox.erp.product.dto.PriceSummary;
import com.orbenox.erp.product.entity.Price;
import com.orbenox.erp.product.mapper.PriceMapper;
import com.orbenox.erp.product.repository.PriceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceService {
    private final PriceRepository priceRepository;
    private final PriceMapper priceMapper;
    private final LocalizationService i18n;

    public List<PriceDto> findAll() {
        return priceMapper.toDtoList(priceRepository.findAllByDeletedFalseOrderByIdAsc());
    }
    
    public List<PriceDto> findAllExcluded(Long idToExclude) {
        List<PriceSummary> summaries = priceRepository.findAllSummaries();
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
        return priceMapper.toDtoList(priceRepository.findAllById(allowedIds));
    }

    public PriceDto findById(Long id) {
        return priceMapper.toDto(priceRepository.findByIdAndDeletedFalse(id));
    }

    public PriceDto create(PriceDto dto) {
        return priceMapper.toDto(priceRepository.save(priceMapper.toEntity(dto)));
    }

    public PriceDto update(Long id, PriceDto dto) {
        Price entity = priceRepository.findByIdAndDeletedFalse(id);
        priceMapper.updateEntityFromDto(dto, entity);
        return priceMapper.toDto(priceRepository.save(entity));
    }

    @Transactional
    public void softDelete(Long id) {
        Price entity = priceRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
        Price saved = priceRepository.save(entity);
        if (!saved.isDeleted()) {
            throw new IllegalStateException(i18n.msg("error.internal"));
        }
    }
}
