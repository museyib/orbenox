package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProducerCreateDto;
import com.orbenox.erp.domain.product.dto.ProducerUpdateDto;
import com.orbenox.erp.domain.product.entity.Producer;
import com.orbenox.erp.domain.product.mapper.ProducerMapper;
import com.orbenox.erp.domain.product.projection.ProducerItem;
import com.orbenox.erp.domain.product.repository.ProducerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.orbenox.erp.config.CacheConfig.CacheNames.LOOKUPS;
import static com.orbenox.erp.config.CacheConfig.CacheNames.PRODUCERS;

@Service
@RequiredArgsConstructor
public class ProducerService {
    private final ProducerRepository producerRepository;
    private final ProducerMapper producerMapper;

    @Cacheable(PRODUCERS)
    public List<ProducerItem> getAllItems() {
        return producerRepository.getAllItems();
    }

    public ProducerItem getItemById(Long id) {
        return producerRepository.getItemById(id);
    }

    @CacheEvict(value = {PRODUCERS, LOOKUPS}, allEntries = true)
    public ProducerItem create(ProducerCreateDto dto) {
        Producer producer = producerRepository.save(producerMapper.toEntity(dto));
        return producerRepository.getItemById(producer.getId());
    }

    @CacheEvict(value = {PRODUCERS, LOOKUPS}, allEntries = true)
    @Transactional
    public ProducerItem update(Long id, ProducerUpdateDto dto) {
        Producer entity = producerRepository.findByIdAndDeletedFalse(id);
        producerMapper.updateEntityFromDto(dto, entity);
        return producerRepository.getItemById(id);
    }

    @CacheEvict(value = {PRODUCERS, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        Producer entity = producerRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}

