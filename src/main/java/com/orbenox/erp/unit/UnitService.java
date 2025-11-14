package com.orbenox.erp.unit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService {
    private final UnitMapper unitMapper;
    private final UnitRepository unitRepository;

    public List<UnitDto> findAll() {
        return unitMapper.toDtoList(unitRepository.findAll());
    }

    public UnitDto findById(Long id) {
        return unitMapper.toDto(unitRepository.findById(id).orElseThrow());
    }

    public UnitDto create(UnitDto unitDto) {
        return unitMapper.toDto(unitRepository.save(unitMapper.toEntity(unitDto)));
    }

    public UnitDto update(Long id, UnitDto unitDto) {
        var unit = unitRepository.findById(id).orElseThrow();
        unitMapper.updateEntityFromDto(unitDto, unit);
        return unitMapper.toDto(unitRepository.save(unit));
    }

    public void deleteById(Long id) {
        unitRepository.deleteById(id);
    }
}
