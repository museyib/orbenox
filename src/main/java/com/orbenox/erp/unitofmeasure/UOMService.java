package com.orbenox.erp.unitofmeasure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UOMService {
    private final UOMMapper uomMapper;
    private final UOMRepository uomRepository;

    public List<UnitOfMeasureDto> findAll() {
        return uomMapper.toDtoList(uomRepository.findAll());
    }

    public UnitOfMeasureDto findById(Long id) {
        return uomMapper.toDto(uomRepository.findById(id).orElseThrow());
    }

    public UnitOfMeasureDto create(UnitOfMeasureDto uomDto) {
        return uomMapper.toDto(uomRepository.save(uomMapper.toEntity(uomDto)));
    }

    public UnitOfMeasureDto update(Long id, UnitOfMeasureDto uomDto) {
        var uom = uomRepository.findById(id).orElseThrow();
        uomMapper.updateEntityFromDto(uomDto, uom);
        return uomMapper.toDto(uomRepository.save(uom));
    }

    public void deleteById(Long id) {
        uomRepository.deleteById(id);
    }
}
