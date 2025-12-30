package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProducerDto;
import com.orbenox.erp.domain.product.entity.Producer;
import com.orbenox.erp.domain.product.mapper.ProducerMapper;
import com.orbenox.erp.domain.product.projection.ProducerItem;
import com.orbenox.erp.domain.product.repository.ProducerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProducerService {
    private final ProducerRepository producerRepository;
    private final ProducerMapper producerMapper;

    public List<ProducerItem> getAllItems() {
        return producerRepository.getAllItems();
    }

    public ProducerItem getItemById(Long id) {
        return producerRepository.getItemById(id);
    }

    public ProducerItem create(ProducerDto dto) {
        Producer producer = producerRepository.save(producerMapper.toEntity(dto));
        return producerRepository.getItemById(producer.getId());
    }

    @Transactional
    public ProducerItem update(Long id, ProducerDto dto) {
        Producer entity = producerRepository.findByIdAndDeletedFalse(id);
        producerMapper.updateEntityFromDto(dto, entity);
        return producerRepository.getItemById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        Producer entity = producerRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
