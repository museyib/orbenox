package com.orbenox.erp.domain.country;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.orbenox.erp.config.CacheConfig.CacheNames.COUNTRIES;
import static com.orbenox.erp.config.CacheConfig.CacheNames.LOOKUPS;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    @Cacheable(COUNTRIES)
    public List<CountryItem> getAllItems() {
        return countryRepository.getAllItems();
    }

    public CountryItem getItemById(Long id) {
        return countryRepository.getItemById(id);
    }

    @CacheEvict(value = {COUNTRIES, LOOKUPS}, allEntries = true)
    public CountryItem create(CountryCreateDto dto) {
        Country country = countryRepository.save(countryMapper.toEntity(dto));
        return countryRepository.getItemById(country.getId());
    }

    @CacheEvict(value = {COUNTRIES, LOOKUPS}, allEntries = true)
    @Transactional
    public CountryItem update(Long id, CountryUpdateDto dto) {
        Country country = countryRepository.findByIdAndDeletedFalse(id);
        countryMapper.updateEntityFromDto(dto, country);
        return countryRepository.getItemById(id);
    }

    @CacheEvict(value = {COUNTRIES, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        Country entity = countryRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}

