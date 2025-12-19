package com.orbenox.erp.common.country;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    public List<CountryItem> findAll() {
        return countryRepository.getAllItems();
    }

    public CountryItem findById(Long id) {
        return countryRepository.getItemById(id);
    }

    public CountryItem create(CountryDto dto) {
        Country country = countryRepository.save(countryMapper.toEntity(dto));
        return countryRepository.getItemById(country.getId());
    }

    @Transactional
    public CountryItem update(Long id, CountryDto dto) {
        Country country = countryRepository.findByIdAndDeletedFalse(id);
        countryMapper.updateEntityFromDto(dto, country);
        return countryRepository.getItemById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        Country entity = countryRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
