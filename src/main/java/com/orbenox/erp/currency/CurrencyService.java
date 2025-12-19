package com.orbenox.erp.currency;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    public List<CurrencyItem> findAll() {
        return currencyRepository.getAllItems();
    }

    public CurrencyItem findById(Long id) {
        return currencyRepository.getItemById(id);
    }

    public CurrencyItem create(CurrencyDto dto) {
        Currency currency = currencyRepository.save(currencyMapper.toEntity(dto));
        return currencyRepository.getItemById(currency.getId());
    }

    @Transactional
    public CurrencyItem update(Long id, CurrencyDto dto) {
        Currency entity = currencyRepository.findByIdAndDeletedFalse(id);
        currencyMapper.updateEntityFromDto(dto, entity);
        return currencyRepository.getItemById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        Currency entity = currencyRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
