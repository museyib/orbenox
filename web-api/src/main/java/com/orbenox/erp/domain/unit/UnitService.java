package com.orbenox.erp.domain.unit;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.orbenox.erp.config.CacheConfig.CacheNames.*;

@Service
@RequiredArgsConstructor
public class UnitService {
    private final UnitMapper unitMapper;
    private final UnitRepository unitRepository;

    @Cacheable(UNITS_ALL)
    public List<UnitItem> getAllItems() {
        return unitRepository.getAllItems();
    }

    @Cacheable(UNITS_BY_DIMENSION_ID)
    public List<UnitItem> findAllByDimensionId(Long dimensionId) {
        return unitRepository.getItemsByUnitDimensionId(dimensionId);
    }

    public UnitItem getItemById(Long id) {
        return unitRepository.getItemById(id);
    }

    @CacheEvict(value = {
            UNITS_ALL,
            UNITS_BY_DIMENSION_ID,
            LOOKUPS}, allEntries = true)
    public UnitItem create(UnitCreateDto dto) {
        Unit unit = unitRepository.save(unitMapper.toEntity(dto));
        return unitRepository.getItemById(unit.getId());
    }

    @CacheEvict(value = {
            UNITS_ALL,
            UNITS_BY_DIMENSION_ID,
            LOOKUPS}, allEntries = true)
    @Transactional
    public UnitItem update(Long id, UnitUpdateDto dto) {
        Unit unit = unitRepository.findByIdAndDeletedFalse(id);
        unitMapper.updateEntityFromDto(dto, unit);
        return unitRepository.getItemById(id);
    }

    @CacheEvict(value = {
            UNITS_ALL,
            UNITS_BY_DIMENSION_ID,
            LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        Unit unit = unitRepository.findByIdAndDeletedFalse(id);
        unit.setDeleted(true);
    }
}

