package com.orbenox.erp.domain.unit.unitdimension;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitDimensionService {
    private final UnitDimensionRepository unitDimensionRepository;

    public List<UnitDimensionItem> getAllItems() {
        return unitDimensionRepository.getAllItems();
    }
}
