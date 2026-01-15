package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.domain.product.dto.ProductBarcodeData;
import com.orbenox.erp.domain.product.dto.ProductBarcodeDto;
import com.orbenox.erp.domain.product.entity.ProductBarcode;
import com.orbenox.erp.domain.product.mapper.ProductBarcodeMapper;
import com.orbenox.erp.domain.product.projection.ProductBarcodeItem;
import com.orbenox.erp.domain.product.projection.SimpleProductItem;
import com.orbenox.erp.domain.product.repository.ProductBarcodeRepository;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.product.request.UpdateProductBarcodeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductBarcodeService {
    private final ProductBarcodeRepository productBarcodeRepository;
    private final ProductRepository productRepository;
    private final ProductBarcodeMapper productBarcodeMapper;

    public ProductBarcodeData getItemsByProductId(Long productId) {
        ProductBarcodeData productBarcodeData = new ProductBarcodeData();
        SimpleProductItem product = productRepository.getSimpleItemById(productId);
        List<ProductBarcodeItem> items = productBarcodeRepository.getItemsByProductId(productId);
        productBarcodeData.setProduct(product);
        productBarcodeData.setBarcodes(items);
        return productBarcodeData;
    }

    public ProductBarcodeData updateBarcodes(UpdateProductBarcodeRequest request) {
        List<Long> idsToUpdate = request.getBarcodesToUpdate().stream().map(ProductBarcodeDto::id).toList();
        List<Long> idsToDelete = request.getBarcodesToDelete().stream().map(ProductBarcodeDto::id).toList();

        Map<Long, ProductBarcode> barcodeMap = productBarcodeRepository.findAllById(idsToUpdate).stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));

        List<ProductBarcode> entityListToUpdate = request.getBarcodesToUpdate().stream().map(item -> {
            ProductBarcode barcode = barcodeMap.get(item.id());
            if (barcode == null) {
                throw new IllegalArgumentException("Barcode not found: " + item.id());
            }
            productBarcodeMapper.updateEntityFromDto(item, barcode);
            return barcode;
        }).toList();

        List<ProductBarcode> entityListToInsert = request.getBarcodesToInsert().stream().map(productBarcodeMapper::toEntity).toList();

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
