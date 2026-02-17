package com.orbenox.erp.domain.warehouse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;


import static com.orbenox.erp.config.CacheConfig.CacheNames.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Cacheable(WAREHOUSES)
    public Slice<WarehouseItem> getAllItems(int page, int size, String search) {
        if (isEmpty(search))
            return warehouseRepository.getAllItems(PageRequest.of(page, size));
        return warehouseRepository.getItemsSearched(PageRequest.of(page, size), search);
    }

    public WarehouseItem getItemById(Long id) {
        return warehouseRepository.getItemById(id);
    }

    @CacheEvict(value = {WAREHOUSES, LOOKUPS}, allEntries = true)
    public WarehouseItem create(WarehouseCreateDto dto) {
        Warehouse warehouse = warehouseRepository.save(warehouseMapper.toEntity(dto));
        return warehouseRepository.getItemById(warehouse.getId());
    }

    @CacheEvict(value = {WAREHOUSES, LOOKUPS}, allEntries = true)
    @Transactional
    public WarehouseItem update(Long id, WarehouseUpdateDto dto) {
        Warehouse entity = warehouseRepository.findByIdAndDeletedFalse(id);
        warehouseMapper.updateEntityFromDto(dto, entity);
        return warehouseRepository.getItemById(id);
    }

    @CacheEvict(value = {WAREHOUSES, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        Warehouse entity = warehouseRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}




