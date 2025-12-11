package com.orbenox.erp.unit;

import com.orbenox.erp.localization.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService {
    private final UnitMapper unitMapper;
    private final UnitRepository unitRepository;
    private final LocalizationService i18n;

    public List<UnitDto> findAll() {
        return unitMapper.toDtoList(unitRepository.findAllByDeletedFalseOrderByIdAsc());
    }

    public List<UnitDto> findAllByDimensionId(Long dimensionId) {
        return unitMapper.toDtoList(unitRepository.findAllByUnitDimensionIdAndDeletedFalseOrderByIdAsc(dimensionId));
    }

    public UnitDto findById(Long id) {
        return unitMapper.toDto(unitRepository.findByIdAndDeletedFalse(id));
    }

    public UnitDto create(UnitDto unitDto) {
        return unitMapper.toDto(unitRepository.save(unitMapper.toEntity(unitDto)));
    }

    public UnitDto update(Long id, UnitDto unitDto) {
        var unit = unitRepository.findByIdAndDeletedFalse(id);
        unitMapper.updateEntityFromDto(unitDto, unit);
        return unitMapper.toDto(unitRepository.save(unit));
    }

    public void softDelete(Long id) {
        Unit unit = unitRepository.findByIdAndDeletedFalse(id);
        unit.setDeleted(true);
        Unit saved = unitRepository.save(unit);
        if (!saved.isDeleted()) {
            throw new IllegalStateException(i18n.msg("error.internal"));
        }
    }
}
