package com.orbenox.erp.product.service;

import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.ProducerDto;
import com.orbenox.erp.product.entity.Producer;
import com.orbenox.erp.product.mapper.ProducerMapper;
import com.orbenox.erp.product.repository.ProducerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProducerService {
    private final ProducerRepository producerRepository;
    private final ProducerMapper producerMapper;
    private final LocalizationService i18n;

    public List<ProducerDto> findAll() {
        return producerMapper.toDtoList(producerRepository.findAllByDeletedFalseOrderByIdAsc());
    }

    public ProducerDto findById(Long id) {
        return producerMapper.toDto(producerRepository.findByIdAndDeletedFalse(id));
    }

    public ProducerDto create(ProducerDto dto) {
        return producerMapper.toDto(producerRepository.save(producerMapper.toEntity(dto)));
    }

    public ProducerDto update(Long id, ProducerDto dto) {
        Producer entity = producerRepository.findByIdAndDeletedFalse(id);
        producerMapper.updateEntityFromDto(dto, entity);
        return producerMapper.toDto(producerRepository.save(entity));
    }

    @Transactional
    public void softDelete(Long id) {
        Producer entity = producerRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
        Producer saved = producerRepository.save(entity);
        if (!saved.isDeleted()) {
            throw new IllegalStateException(i18n.msg("error.internal"));
        }
    }
}
