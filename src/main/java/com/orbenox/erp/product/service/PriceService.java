package com.orbenox.erp.product.service;

import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.PriceDto;
import com.orbenox.erp.product.entity.Price;
import com.orbenox.erp.product.mapper.PriceMapper;
import com.orbenox.erp.product.repository.PriceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceService {
    private final PriceRepository priceRepository;
    private final PriceMapper priceMapper;
    private final LocalizationService i18n;

    public List<PriceDto> findAll() {
        return priceMapper.toDtoList(priceRepository.findAllByDeletedFalse());
    }

    public PriceDto findById(Long id) {
        return priceMapper.toDto(priceRepository.findByIdAndDeletedFalse(id));
    }

    public PriceDto create(PriceDto dto) {
        return priceMapper.toDto(priceRepository.save(priceMapper.toEntity(dto)));
    }

    public PriceDto update(Long id, PriceDto dto) {
        Price entity = priceRepository.findByIdAndDeletedFalse(id);
        priceMapper.updateEntityFromDto(dto, entity);
        return priceMapper.toDto(priceRepository.save(entity));
    }

    @Transactional
    public void softDelete(Long id) {
        Price entity = priceRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
        Price saved = priceRepository.save(entity);
        if (!saved.isDeleted()) {
            throw new IllegalStateException(i18n.msg("error.internal"));
        }
    }
}
