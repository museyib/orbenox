package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.product.dto.ProductDto;
import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.entity.ProductBarcode;
import com.orbenox.erp.domain.product.mapper.ProductMapper;
import com.orbenox.erp.domain.product.projection.ProductItem;
import com.orbenox.erp.domain.product.repository.ProductBarcodeRepository;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductBarcodeRepository productBarcodeRepository;
    private final ProductMapper productMapper;

    public List<ProductItem> getAllItems() {
        return productRepository.getAllItems();
    }

    public ProductItem getItemById(Long id) {
        return productRepository.getItemById(id);
    }

    public ProductItem create(ProductDto dto) {
        Product product = productRepository.save(productMapper.toEntity(dto));
        addToBarcodeList(product);
        return productRepository.getItemById(product.getId());
    }

    @Transactional
    public ProductItem update(Long id, ProductDto dto) {
        Product product = productRepository.findByIdAndDeletedFalse(id);
        productMapper.updateEntityFromDto(dto, product);
        if (productBarcodeRepository.findByBarcode(product.getDefaultBarcode()).isEmpty()) {
            addToBarcodeList(product);
        }
        return productRepository.getItemById(id);
    }

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
