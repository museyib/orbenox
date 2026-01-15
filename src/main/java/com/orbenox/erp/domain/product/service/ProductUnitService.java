package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.domain.product.dto.ProductUnitData;
import com.orbenox.erp.domain.product.dto.ProductUnitDto;
import com.orbenox.erp.domain.product.entity.ProductUnit;
import com.orbenox.erp.domain.product.mapper.ProductUnitMapper;
import com.orbenox.erp.domain.product.projection.ProductUnitItem;
import com.orbenox.erp.domain.product.projection.SimpleProductItem;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.product.repository.ProductUnitRepository;
import com.orbenox.erp.domain.product.request.UpdateProductUnitRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductUnitService {
    private final ProductUnitRepository productUnitRepository;
    private final ProductRepository productRepository;
    private final ProductUnitMapper productUnitMapper;

    public ProductUnitData getItemsByProductId(Long productId) {
        ProductUnitData data = new ProductUnitData();
        SimpleProductItem product = productRepository.getSimpleItemById(productId);
        List<ProductUnitItem> items = productUnitRepository.getItemsByProductId(productId);
        data.setProduct(product);
        data.setUnits(items);
        return data;
    }

    public ProductUnitData updateProductUnits(UpdateProductUnitRequest request) {
        List<Long> idsToUpdate = request.getUnitsToUpdate().stream().map(ProductUnitDto::id).toList();
        List<Long> idsToDelete = request.getUnitsToDelete().stream().map(ProductUnitDto::id).toList();

        Map<Long, ProductUnit> unitMap = productUnitRepository.findAllById(idsToUpdate).stream()
                .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));

        List<ProductUnit> entityListToUpdate = request.getUnitsToUpdate().stream()
                .map(item -> {
                    ProductUnit unit = unitMap.get(item.id());

                    if (unit == null) {
                        throw new IllegalArgumentException("Unit not found: " + item.id());
                    }
                    productUnitMapper.updateEntityFromDto(item, unit);
                    return unit;
                }).toList();
        List<ProductUnit> entityListToInsert = request.getUnitsToInsert().stream()
                .map(productUnitMapper::toEntity).toList();

        List<ProductUnit> toSave = new ArrayList<>(entityListToInsert);
        toSave.addAll(entityListToUpdate);
        productUnitRepository.saveAll(toSave);
        productUnitRepository.deleteAllById(idsToDelete);

        ProductUnitData data = new ProductUnitData();
        List<ProductUnitItem> items = productUnitRepository.getItemsByProductId(request.getProductId());
        SimpleProductItem product = items.isEmpty()
                ? productRepository.getSimpleItemById(request.getProductId())
                : items.get(0).getProduct();
        data.setProduct(product);
        data.setUnits(items);
        return data;
    }
}
