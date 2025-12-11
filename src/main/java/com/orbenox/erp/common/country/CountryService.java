package com.orbenox.erp.common.country;

import com.orbenox.erp.localization.LocalizationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final LocalizationService i18n;

    public List<CountryDto> findAll() {
        return countryMapper.toDtoList(countryRepository.findAllByDeletedFalseOrderByIdAsc());
    }

    public CountryDto findById(Long id) {
        return countryMapper.toDto(countryRepository.findByIdAndDeletedFalse(id));
    }

    public CountryDto create(CountryDto dto) {
        return countryMapper.toDto(countryRepository.save(countryMapper.toEntity(dto)));
    }

    public CountryDto update(Long id, CountryDto dto) {
        Country country = countryRepository.findByIdAndDeletedFalse(id);
        countryMapper.updateEntityFromDto(dto, country);
        return countryMapper.toDto(countryRepository.save(country));
    }

    @Transactional
    public void softDelete(Long id) {
        Country entity = countryRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
        Country saved  = countryRepository.save(entity);
        if (!saved.isDeleted()) {
            throw new IllegalStateException(i18n.msg("error.internal"));
        }
    }
}
