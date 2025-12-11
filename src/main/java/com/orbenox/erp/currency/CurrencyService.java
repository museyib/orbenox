package com.orbenox.erp.currency;

import com.orbenox.erp.localization.LocalizationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;
    private final LocalizationService i18n;

    public List<CurrencyDto> findAll() {
        return currencyMapper.toDtoList(currencyRepository.findAllByDeletedFalseOrderByIdAsc());
    }

    public CurrencyDto findById(Long id) {
        return currencyMapper.toDto(currencyRepository.findByIdAndDeletedFalse(id));
    }

    public CurrencyDto create(CurrencyDto dto) {
        return currencyMapper.toDto(currencyRepository.save(currencyMapper.toEntity(dto)));
    }

    public CurrencyDto update(Long id, CurrencyDto dto) {
        Currency entity = currencyRepository.findByIdAndDeletedFalse(id);
        currencyMapper.updateEntityFromDto(dto, entity);
        return currencyMapper.toDto(currencyRepository.save(entity));
    }

    @Transactional
    public void softDelete(Long id) {
        Currency entity = currencyRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
        Currency saved = currencyRepository.save(entity);
        if (!saved.isDeleted()) {
            throw new IllegalStateException(i18n.msg("error.internal"));
        }
    }
}
