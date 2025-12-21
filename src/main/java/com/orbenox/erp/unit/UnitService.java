package com.orbenox.erp.unit;

import com.orbenox.erp.unit.unitdimension.UnitDimension;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService {
    private final UnitMapper unitMapper;
    private final UnitRepository unitRepository;
    private final EntityManager em;

    public List<UnitItem> getAllItems() {
        return unitRepository.getAllItems();
    }

    public List<UnitItem> findAllByDimensionId(Long dimensionId) {
        return unitRepository.getItemsByUnitDimensionId(dimensionId);
    }

    public UnitItem findById(Long id) {
        return unitRepository.getItemById(id);
    }

    public UnitItem create(UnitDto unitDto) {
        Unit unit = unitRepository.save(unitMapper.toEntity(unitDto));
        return unitRepository.getItemById(unit.getId());
    }

    @Transactional
    public UnitItem update(Long id, UnitDto unitDto) {
        Unit unit = unitRepository.findByIdAndDeletedFalse(id);
        unitMapper.updateEntityFromDto(unitDto, unit);
        UnitDimension unitDimension = em.getReference(UnitDimension.class, unitDto.unitDimension().id());
        unit.setUnitDimension(unitDimension);
        return unitRepository.getItemById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        Unit unit = unitRepository.findByIdAndDeletedFalse(id);
        unit.setDeleted(true);
    }
}
