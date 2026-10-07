package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.domain.product.dto.ProductBarcodeUpdateDto;
import com.orbenox.erp.domain.product.entity.ProductBarcode;
import com.orbenox.erp.domain.product.mapper.ProductBarcodeMapper;
import com.orbenox.erp.domain.product.projection.ProductBarcodeData;
import com.orbenox.erp.domain.product.projection.ProductBarcodeItem;
import com.orbenox.erp.domain.product.projection.SimpleProductItem;
import com.orbenox.erp.domain.product.repository.ProductBarcodeRepository;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.product.request.UpdateProductBarcodeRequest;
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

import static com.orbenox.erp.config.CacheConfig.CacheNames.PRODUCT_BARCODES;

@Service
@RequiredArgsConstructor
public class ProductBarcodeService {
    private final ProductBarcodeRepository productBarcodeRepository;
    private final ProductRepository productRepository;
    private final ProductBarcodeMapper productBarcodeMapper;
    private final LocalizationService i18n;

    @Cacheable(PRODUCT_BARCODES)
    public ProductBarcodeData getItemsByProductId(Long productId) {
        ProductBarcodeData productBarcodeData = new ProductBarcodeData();
        SimpleProductItem product = productRepository.getSimpleItemById(productId);
        List<ProductBarcodeItem> items = productBarcodeRepository.getItemsByProductId(productId);
        productBarcodeData.setProduct(product);
        productBarcodeData.setBarcodes(items);
        return productBarcodeData;
    }

    @CacheEvict(value = PRODUCT_BARCODES, allEntries = true)
    public ProductBarcodeData updateBarcodes(UpdateProductBarcodeRequest request) {
        List<Long> idsToUpdate = request.getBarcodesToUpdate().stream().map(ProductBarcodeUpdateDto::id).toList();
        List<Long> idsToDelete = request.getBarcodesToDelete().stream().map(ProductBarcodeUpdateDto::id).toList();

        Map<Long, ProductBarcode> barcodeMap = productBarcodeRepository.findAllById(idsToUpdate).stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));

        List<ProductBarcode> entityListToUpdate = request.getBarcodesToUpdate().stream().map(item -> {
            ProductBarcode barcode = barcodeMap.get(item.id());
            if (barcode == null) {
                throw new IllegalArgumentException(i18n.msg("barcode.notFound", item.barcode()));
            }
            productBarcodeMapper.updateEntityFromDto(item, barcode);
            return barcode;
        }).toList();

        List<ProductBarcode> entityListToInsert = request.getBarcodesToInsert().stream()
                .map(productBarcodeMapper::toEntity).toList();

        List<ProductBarcode> toSave = new ArrayList<>(entityListToInsert);
        toSave.addAll(entityListToUpdate);
        productBarcodeRepository.saveAll(toSave);
        productBarcodeRepository.deleteAllById(idsToDelete);

        ProductBarcodeData productBarcodeData = new ProductBarcodeData();
        List<ProductBarcodeItem> items = productBarcodeRepository.getItemsByProductId(request.getProductId());
        SimpleProductItem product = items.isEmpty() ? productRepository.getSimpleItemById(request.getProductId()) : items.get(0).getProduct();
        productBarcodeData.setProduct(product);
        productBarcodeData.setBarcodes(items);
        return productBarcodeData;
    }
}

