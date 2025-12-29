package com.orbenox.erp.product.service;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.product.dto.ProductPricingData;
import com.orbenox.erp.product.entity.ProductPrice;
import com.orbenox.erp.product.mapper.ProductPriceMapper;
import com.orbenox.erp.product.projection.ProductPriceItem;
import com.orbenox.erp.product.projection.SimpleProductItem;
import com.orbenox.erp.product.repository.ProductPriceRepository;
import com.orbenox.erp.product.repository.ProductRepository;
import com.orbenox.erp.product.dto.ProductPriceDto;
import com.orbenox.erp.product.request.UpdateProductPriceRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductPriceService {
    private final ProductPriceRepository productPriceRepository;
    private final ProductRepository productRepository;
    private final ProductPriceMapper productPriceMapper;

    @Cacheable("productPrices")
    public ProductPricingData getPriceDataByProductId(Long productId) {
        SimpleProductItem product = productRepository.getSimpleItemById(productId);
        List<ProductPriceItem> priceSummaries = productPriceRepository.getItemsByProductId(productId);
        ProductPricingData pricingData = new ProductPricingData();
        pricingData.setProduct(product);
        pricingData.setPrices(priceSummaries);
        return pricingData;
    }

    @Transactional
    public ProductPricingData updateProductPrices(UpdateProductPriceRequest request) {
        List<Long> ids = request.getPriceListToUpdate().stream().map(ProductPriceDto::id).toList();
        Map<Long, ProductPrice> priceMap = productPriceRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
        List<ProductPrice> entityListToUpdate = request.getPriceListToUpdate().stream()
                .map(item -> {
                    ProductPrice entity = priceMap.get(item.id());
                    if (entity == null)
                        throw new IllegalArgumentException("Product price not found: " + item.id());
                    productPriceMapper.updateEntityFromDto(item, entity);
                    return entity;
                }).toList();
        List<ProductPrice> entityListToInsert = request.getPriceListToInsert().stream()
                .map(productPriceMapper::toEntity).toList();
        List<ProductPrice> toSave = new ArrayList<>(entityListToUpdate);
        toSave.addAll(entityListToInsert);
        productPriceRepository.saveAll(toSave);
        List<ProductPriceItem> items = productPriceRepository.getItemsByProductId(request.getProductId());
        ProductPricingData productPricingData = new ProductPricingData();
        SimpleProductItem productItem = items.isEmpty() ?
                productRepository.getSimpleItemById(request.getProductId()) :
                items.get(0).getProduct();
        productPricingData.setProduct(productItem);
        productPricingData.setPrices(items);
        return productPricingData;
    }
}
