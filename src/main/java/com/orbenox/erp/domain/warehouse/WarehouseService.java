package com.orbenox.erp.domain.warehouse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Cacheable("warehouses")
    public List<WarehouseItem> getAllItems() {
        return warehouseRepository.getAllItems();
    }

    public WarehouseItem getItemById(Long id) {
        return warehouseRepository.getItemById(id);
    }

    @CacheEvict(value = {"warehouses", "lookups"}, allEntries = true)
    public WarehouseItem create(WarehouseCreateDto dto) {
        Warehouse warehouse = warehouseRepository.save(warehouseMapper.toEntity(dto));
        return warehouseRepository.getItemById(warehouse.getId());
    }

    @CacheEvict(value = {"warehouses", "lookups"}, allEntries = true)
    @Transactional
    public WarehouseItem update(Long id, WarehouseUpdateDto dto) {
        Warehouse entity = warehouseRepository.findByIdAndDeletedFalse(id);
        warehouseMapper.updateEntityFromDto(dto, entity);
        return warehouseRepository.getItemById(id);
    }

    @CacheEvict(value = {"warehouses", "lookups"}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        Warehouse entity = warehouseRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
