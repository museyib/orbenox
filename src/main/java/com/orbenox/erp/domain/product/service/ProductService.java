package com.orbenox.erp.domain.product.service;

import com.orbenox.erp.domain.country.Country;
import com.orbenox.erp.domain.product.dto.ProductDto;
import com.orbenox.erp.domain.product.entity.*;
import com.orbenox.erp.domain.product.mapper.ProductMapper;
import com.orbenox.erp.domain.product.projection.ProductBarcodeItem;
import com.orbenox.erp.domain.product.projection.ProductItem;
import com.orbenox.erp.domain.product.repository.ProductBarcodeRepository;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.unit.Unit;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductBarcodeRepository productBarcodeRepository;
    private final ProductMapper productMapper;
    private final EntityManager em;

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

        Brand brand = em.getReference(Brand.class, dto.brand().id());
        ProductClass productClass = em.getReference(ProductClass.class, dto.productClass().id());
        ProductType productType = em.getReference(ProductType.class, dto.productType().id());
        ProductCategory productCategory = em.getReference(ProductCategory.class, dto.productCategory().id());
        ProductGroup productGroup = em.getReference(ProductGroup.class, dto.productGroup().id());
        Producer producer = em.getReference(Producer.class, dto.producer().id());
        Country country = em.getReference(Country.class, dto.country().id());
        Unit defaultUnit = em.getReference(Unit.class, dto.defaultUnit().id());

        product.setBrand(brand);
        product.setProductClass(productClass);
        product.setProductType(productType);
        product.setProductCategory(productCategory);
        product.setProductGroup(productGroup);
        product.setProducer(producer);
        product.setCountry(country);
        product.setDefaultUnit(defaultUnit);

        Optional<ProductBarcodeItem> barcodeItem = productBarcodeRepository.getItemByBarcode(product.getDefaultBarcode());

        if (barcodeItem.isEmpty()) {
            addToBarcodeList(product);
        } else if (!barcodeItem.get().getUnit().getId().equals(dto.defaultUnit().id())) {
            ProductBarcode barcode = productBarcodeRepository.findByBarcode(product.getDefaultBarcode());
            barcode.setUnit(defaultUnit);
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
