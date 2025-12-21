package com.orbenox.erp.product.service;

import com.orbenox.erp.product.dto.ProducerDto;
import com.orbenox.erp.product.entity.Producer;
import com.orbenox.erp.product.mapper.ProducerMapper;
import com.orbenox.erp.product.projection.ProducerItem;
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

    public List<ProducerItem> findAll() {
        return producerRepository.getAllItems();
    }

    public ProducerItem findById(Long id) {
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
