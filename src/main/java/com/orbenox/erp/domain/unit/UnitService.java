package com.orbenox.erp.domain.unit;

import com.orbenox.erp.domain.unit.unitdimension.UnitDimension;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService {
    private final UnitMapper unitMapper;
    private final UnitRepository unitRepository;
    private final EntityManager em;

    @Cacheable("units.all")
    public List<UnitItem> getAllItems() {
        return unitRepository.getAllItems();
    }

    @Cacheable("units.byDimensionId")
    public List<UnitItem> findAllByDimensionId(Long dimensionId) {
        return unitRepository.getItemsByUnitDimensionId(dimensionId);
    }

    public UnitItem getItemById(Long id) {
        return unitRepository.getItemById(id);
    }

    @CacheEvict(value = {
            "units.all",
            "units.byDimensionId",
            "lookups"}, allEntries = true)
    public UnitItem create(UnitCreateDto dto) {
        Unit unit = unitRepository.save(unitMapper.toEntity(dto));
        return unitRepository.getItemById(unit.getId());
    }

    @CacheEvict(value = {
            "units.all",
            "units.byDimensionId",
            "lookups"}, allEntries = true)
    @Transactional
    public UnitItem update(Long id, UnitUpdateDto dto) {
        Unit unit = unitRepository.findByIdAndDeletedFalse(id);
        unitMapper.updateEntityFromDto(dto, unit);
        UnitDimension unitDimension = em.getReference(UnitDimension.class, dto.unitDimension().id());
        unit.setUnitDimension(unitDimension);
        return unitRepository.getItemById(id);
    }

    @CacheEvict(value = {
            "units.all",
            "units.byDimensionId",
            "lookups"}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        Unit unit = unitRepository.findByIdAndDeletedFalse(id);
        unit.setDeleted(true);
    }
}
