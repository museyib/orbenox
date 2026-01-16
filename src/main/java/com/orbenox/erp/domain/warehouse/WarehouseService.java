package com.orbenox.erp.domain.warehouse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    public List<WarehouseItem> getAllItems() {
        return warehouseRepository.getAllItems();
    }

    public WarehouseItem getItemById(Long id) {
        return warehouseRepository.getItemById(id);
    }

    public WarehouseItem create(WarehouseDto dto) {
        Warehouse warehouse = warehouseRepository.save(warehouseMapper.toEntity(dto));
        return warehouseRepository.getItemById(warehouse.getId());
    }

    @Transactional
    public WarehouseItem update(Long id, WarehouseDto dto) {
        Warehouse entity = warehouseRepository.findByIdAndDeletedFalse(id);
        warehouseMapper.updateEntityFromDto(dto, entity);
        return warehouseRepository.getItemById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        Warehouse entity = warehouseRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }
}
