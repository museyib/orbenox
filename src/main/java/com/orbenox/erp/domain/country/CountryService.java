package com.orbenox.erp.domain.country;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    @Cacheable("countries")
    public List<CountryItem> getAllItems() {
        return countryRepository.getAllItems();
    }

    public CountryItem getItemById(Long id) {
        return countryRepository.getItemById(id);
    }

    @CacheEvict(value = {"countries", "lookups"}, allEntries = true)
    public CountryItem create(CountryCreateDto dto) {
        Country country = countryRepository.save(countryMapper.toEntity(dto));
        return countryRepository.getItemById(country.getId());
    }

    @CacheEvict(value = {"countries", "lookups"}, allEntries = true)
    @Transactional
    public CountryItem update(Long id, CountryDto dto) {
        Country country = countryRepository.findByIdAndDeletedFalse(id);
        countryMapper.updateEntityFromDto(dto, country);
        return countryRepository.getItemById(id);
    }

    @CacheEvict(value = {"countries", "lookups"}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        Country entity = countryRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
