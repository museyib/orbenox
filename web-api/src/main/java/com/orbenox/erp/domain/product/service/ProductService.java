package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductCreateDto;
import com.orbenox.erp.domain.product.dto.ProductUpdateDto;
import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.entity.ProductBarcode;
import com.orbenox.erp.domain.product.mapper.ProductMapper;
import com.orbenox.erp.domain.product.projection.ProductBarcodeItem;
import com.orbenox.erp.domain.product.projection.ProductItem;
import com.orbenox.erp.domain.product.repository.ProductBarcodeRepository;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;


import static com.orbenox.erp.config.CacheConfig.CacheNames.*;
import static org.springframework.util.ObjectUtils.isEmpty;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductBarcodeRepository productBarcodeRepository;
    private final ProductMapper productMapper;

    @Cacheable(PRODUCTS)
    public Slice<ProductItem> getAllItems(int page, int size, String search) {
        if (isEmpty(search))
            return productRepository.getAllItems(PageRequest.of(page, size));
        return productRepository.getItemsSearched(PageRequest.of(page, size), search);
    }

    public ProductItem getItemById(Long id) {
        return productRepository.getItemById(id);
    }

    @CacheEvict(value = {PRODUCTS, PRODUCT_BARCODES, LOOKUPS}, allEntries = true)
    public ProductItem create(ProductCreateDto dto) {
        Product product = productRepository.save(productMapper.toEntity(dto));
        addToBarcodeList(product);
        return productRepository.getItemById(product.getId());
    }

    @CacheEvict(value = {PRODUCTS, PRODUCT_BARCODES, LOOKUPS}, allEntries = true)
    @Transactional
    public ProductItem update(Long id, ProductUpdateDto dto) {
        Product product = productRepository.findByIdAndDeletedFalse(id);
        productMapper.updateEntityFromDto(dto, product);

        Optional<ProductBarcodeItem> barcodeItem = productBarcodeRepository.getItemByBarcode(product.getDefaultBarcode());

        if (barcodeItem.isEmpty()) {
            addToBarcodeList(product);
        } else if (!barcodeItem.get().getUnit().getId().equals(dto.defaultUnitId())) {
            ProductBarcode barcode = productBarcodeRepository.findByBarcode(product.getDefaultBarcode());
            barcode.setUnit(product.getDefaultUnit());
        }
        return productRepository.getItemById(id);
    }

    @CacheEvict(value = {PRODUCTS, PRODUCT_BARCODES, LOOKUPS}, allEntries = true)
    @Transactional
    public void softDelete(Long id) {
        Product entity = productRepository.findByIdAndDeletedFalse(id);
        entity.setDeleted(true);
    }

    private void addToBarcodeList(Product product) {
        ProductBarcode productBarcode = new ProductBarcode();
        productBarcode.setProduct(product);
        productBarcode.setUnit(product.getDefaultUnit());
        productBarcode.setBarcode(product.getDefaultBarcode());
        productBarcodeRepository.save(productBarcode);
    }
}




