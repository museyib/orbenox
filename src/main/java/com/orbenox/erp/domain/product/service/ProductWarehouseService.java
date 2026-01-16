package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.domain.product.dto.ProductWarehouseData;
import com.orbenox.erp.domain.product.dto.ProductWarehouseDto;
import com.orbenox.erp.domain.product.entity.ProductWarehouse;
import com.orbenox.erp.domain.product.mapper.ProductWarehouseMapper;
import com.orbenox.erp.domain.product.projection.ProductWarehouseItem;
import com.orbenox.erp.domain.product.projection.SimpleProductItem;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.product.repository.ProductWarehouseRepository;
import com.orbenox.erp.domain.product.request.UpdateProductWarehouseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductWarehouseService {
    private final ProductWarehouseRepository productWarehouseRepository;
    private final ProductRepository productRepository;
    private final ProductWarehouseMapper productWarehouseMapper;

    public ProductWarehouseData getItemsByProductId(Long productId) {
        ProductWarehouseData data = new ProductWarehouseData();
        SimpleProductItem product = productRepository.getSimpleItemById(productId);
        List<ProductWarehouseItem> items = productWarehouseRepository.getItemsByProductId(productId);
        data.setProduct(product);
        data.setWarehouses(items);
        return data;
    }

    public ProductWarehouseData updateProductWarehouses(UpdateProductWarehouseRequest request) {
        List<Long> idsToUpdate = request.getWarehousesToUpdate().stream().map(ProductWarehouseDto::id).toList();
        List<Long> idsToDelete = request.getWarehousesToDelete().stream().map(ProductWarehouseDto::id).toList();

        Map<Long, ProductWarehouse> warehouseMap = productWarehouseRepository.findAllById(idsToUpdate).stream()
                .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));

        List<ProductWarehouse> entityListToUpdate = request.getWarehousesToUpdate().stream()
                .map(item -> {
                    ProductWarehouse warehouse = warehouseMap.get(item.id());

                    if (warehouse == null) {
                        throw new IllegalArgumentException("Warehouse not found: " + item.id());
                    }
                    productWarehouseMapper.updateEntityFromDto(item, warehouse);
                    return warehouse;
                }).toList();
        List<ProductWarehouse> entityListToInsert = request.getWarehousesToInsert().stream()
                .map(productWarehouseMapper::toEntity).toList();

        List<ProductWarehouse> toSave = new ArrayList<>(entityListToInsert);
        toSave.addAll(entityListToUpdate);
        productWarehouseRepository.saveAll(toSave);
        productWarehouseRepository.deleteAllById(idsToDelete);

        ProductWarehouseData data = new ProductWarehouseData();
        List<ProductWarehouseItem> items = productWarehouseRepository.getItemsByProductId(request.getProductId());
        SimpleProductItem product = items.isEmpty()
                ? productRepository.getSimpleItemById(request.getProductId())
                : items.get(0).getProduct();
        data.setProduct(product);
        data.setWarehouses(items);
        return data;
    }
}
