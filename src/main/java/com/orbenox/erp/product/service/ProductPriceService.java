package com.orbenox.erp.product.service;

import com.orbenox.erp.common.entity.BaseEntity;
import com.orbenox.erp.price.PriceList;
import com.orbenox.erp.product.dto.ProductPricingData;
import com.orbenox.erp.product.entity.Product;
import com.orbenox.erp.product.entity.ProductPrice;
import com.orbenox.erp.product.projection.ProductPriceItem;
import com.orbenox.erp.product.projection.SimpleProductItem;
import com.orbenox.erp.product.repository.ProductPriceRepository;
import com.orbenox.erp.product.request.ProductPriceRequest;
import com.orbenox.erp.product.request.UpdateProductPriceRequest;
import com.orbenox.erp.unit.Unit;
import jakarta.persistence.EntityManager;
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
    private final EntityManager entityManager;

    @Cacheable("productPrices")
    public ProductPricingData getPriceDataByProductId(Long productId) {
        SimpleProductItem product = productPriceRepository.getProductItemByProductId(productId);
        List<ProductPriceItem> priceSummaries = productPriceRepository.getItemsByProductId(productId);
        ProductPricingData pricingData = new ProductPricingData();
        pricingData.setProduct(product);
        pricingData.setPrices(priceSummaries);
        return pricingData;
    }

    @Transactional
    public ProductPricingData updateProductPrices(UpdateProductPriceRequest request) {
        List<Long> ids = request.getPriceListToUpdate().stream().map(ProductPriceRequest::getId).toList();
        Map<Long, ProductPrice> priceMap = productPriceRepository.findAllById(ids).stream().collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
        List<ProductPrice> entityListToUpdate = request.getPriceListToUpdate().stream()
                .map(item -> {
                    ProductPrice entity = priceMap.get(item.getId());
                    if (entity == null)
                        throw new IllegalArgumentException("Product price not found: " + item.getId());
                    entity.setPrice(item.getPrice());
                    entity.setFactorToParent(item.getFactorToParent());
                    entity.setFixedPrice(item.getFixedPrice());
                    entity.setRoundLength(item.getRoundLength());
                    entity.setDiscountRatioLimit(item.getDiscountRatioLimit());
                    return entity;
                }).toList();
        Product product = entityManager.getReference(Product.class, request.getProduct().getId());
        List<ProductPrice> entityListToInsert = request.getPriceListToInsert().stream()
                .map(item -> {
                    PriceList priceList = entityManager.getReference(PriceList.class, item.getPriceList().getId());
                    Unit unit = entityManager.getReference(Unit.class, item.getUnit().getId());
                    ProductPrice entity = new ProductPrice();
                    entity.setProduct(product);
                    entity.setUnit(unit);
                    entity.setPriceList(priceList);
                    entity.setPrice(item.getPrice());
                    entity.setFactorToParent(item.getFactorToParent());
                    entity.setFixedPrice(item.getFixedPrice());
                    entity.setRoundLength(item.getRoundLength());
                    entity.setDiscountRatioLimit(item.getDiscountRatioLimit());
                    return entity;
                }).toList();
        List<ProductPrice> toSave = new ArrayList<>();
        toSave.addAll(entityListToUpdate);
        toSave.addAll(entityListToInsert);
        productPriceRepository.saveAll(toSave);
        List<ProductPriceItem> items = productPriceRepository.getItemsByProductId(request.getProduct().getId());
        ProductPricingData productPricingData = new ProductPricingData();
        SimpleProductItem productItem = items.isEmpty() ?
                productPriceRepository.getProductItemByProductId(request.getProduct().getId()) :
                items.get(0).getProduct();
        productPricingData.setProduct(productItem);
        productPricingData.setPrices(items);
        return productPricingData;
    }
}
