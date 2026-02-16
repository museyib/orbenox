package com.orbenox.erp.domain.unit.unitdimension;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.orbenox.erp.config.CacheConfig.CacheNames.LOOKUPS;
import static com.orbenox.erp.config.CacheConfig.CacheNames.UNIT_DIMENSIONS;

@Service
@RequiredArgsConstructor
public class UnitDimensionService {
    private final UnitDimensionRepository unitDimensionRepository;
    private final UnitDimensionMapper unitDimensionMapper;

    @Cacheable(UNIT_DIMENSIONS)
    public List<UnitDimensionItem> getAllItems() {
        return unitDimensionRepository.getAllItems();
    }

    public UnitDimensionItem getItemById(Long id) {
        return unitDimensionRepository.getItemById(id);
    }

    @CacheEvict(value = {UNIT_DIMENSIONS, LOOKUPS}, allEntries = true)
    public UnitDimensionItem create(UnitDimensionDto dto) {
        UnitDimension entity = unitDimensionRepository.save(unitDimensionMapper.toEntity(dto));
        return unitDimensionRepository.getItemById(entity.getId());
    }

    @CacheEvict(value = {UNIT_DIMENSIONS, LOOKUPS}, allEntries = true)
    @Transactional
    public UnitDimensionItem update(Long id, UnitDimensionDto dto) {
        UnitDimension entity = unitDimensionRepository.findByIdAndDeletedFalse(id);
        unitDimensionMapper.updateEntityFromDto(dto, entity);
        return unitDimensionRepository.getItemById(id);
    }

    @CacheEvict(value = {UNIT_DIMENSIONS, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        UnitDimension entity = unitDimensionRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
