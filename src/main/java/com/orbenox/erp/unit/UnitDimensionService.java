package com.orbenox.erp.unit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitDimensionService {
    private final UnitDimensionRepository unitDimensionRepository;
    private final UnitDimensionMapper unitDimensionMapper;

    public List<UnitDimensionDto> findAll() {
        return unitDimensionMapper.toDtoList(unitDimensionRepository.findAll());
    }
}
