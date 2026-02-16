package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.domain.product.dto.ProductUnitUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductUnit;
import com.orbenox.erp.domain.product.mapper.ProductUnitMapper;
import com.orbenox.erp.domain.product.projection.ProductUnitData;
import com.orbenox.erp.domain.product.projection.ProductUnitItem;
import com.orbenox.erp.domain.product.projection.SimpleProductItem;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.product.repository.ProductUnitRepository;
import com.orbenox.erp.domain.product.request.UpdateProductUnitRequest;
import com.orbenox.erp.localization.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.orbenox.erp.config.CacheConfig.CacheNames.PRODUCT_UNITS;

@Service
@RequiredArgsConstructor
public class ProductUnitService {
    private final ProductUnitRepository productUnitRepository;
    private final ProductRepository productRepository;
    private final ProductUnitMapper productUnitMapper;
    private final LocalizationService i18n;

    @Cacheable(PRODUCT_UNITS)
    public ProductUnitData getItemsByProductId(Long productId) {
        ProductUnitData data = new ProductUnitData();
        SimpleProductItem product = productRepository.getSimpleItemById(productId);
        List<ProductUnitItem> items = productUnitRepository.getItemsByProductId(productId);
        data.setProduct(product);
        data.setUnits(items);
        return data;
    }

    @CacheEvict(value = PRODUCT_UNITS, allEntries = true)
    public ProductUnitData updateProductUnits(UpdateProductUnitRequest request) {
        List<Long> idsToUpdate = request.getUnitsToUpdate().stream().map(ProductUnitUpdateDto::id).toList();
        List<Long> idsToDelete = request.getUnitsToDelete().stream().map(ProductUnitUpdateDto::id).toList();

        Map<Long, ProductUnit> unitMap = productUnitRepository.findAllById(idsToUpdate).stream()
                .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));

        List<ProductUnit> entityListToUpdate = request.getUnitsToUpdate().stream()
                .map(item -> {
                    ProductUnit unit = unitMap.get(item.id());

                    if (unit == null) {
                        throw new IllegalArgumentException(i18n.msg("unit.notFound", item.unitId()));
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

