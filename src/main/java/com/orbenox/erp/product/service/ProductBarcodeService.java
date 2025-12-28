package com.orbenox.erp.product.service;

import com.orbenox.erp.product.dto.ProductBarcodeData;
import com.orbenox.erp.product.projection.ProductBarcodeItem;
import com.orbenox.erp.product.projection.SimpleProductItem;
import com.orbenox.erp.product.repository.ProductBarcodeRepository;
import com.orbenox.erp.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductBarcodeService {
    private final ProductBarcodeRepository productBarcodeRepository;
    private final ProductRepository productRepository;

    public ProductBarcodeData getItemsByProductId(Long productId) {
        ProductBarcodeData productBarcodeData = new ProductBarcodeData();
        SimpleProductItem product = productRepository.getSimpleItemById(productId);
        List<ProductBarcodeItem> items = productBarcodeRepository.getItemsByProductId(productId);
        productBarcodeData.setProduct(product);
        productBarcodeData.setBarcodes(items);
        return productBarcodeData;
    }
}
