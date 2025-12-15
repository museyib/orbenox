package com.orbenox.erp.product.service;

import com.orbenox.erp.price.PriceList;
import com.orbenox.erp.product.dto.*;
import com.orbenox.erp.product.entity.Product;
import com.orbenox.erp.product.entity.ProductPriceList;
import com.orbenox.erp.product.mapper.ProductPriceListMapper;
import com.orbenox.erp.product.repository.ProductPriceListRepository;
import com.orbenox.erp.product.repository.ProductRepository;
import com.orbenox.erp.product.request.UpdateProductPriceRequest;
import com.orbenox.erp.product.summary.PriceSummary;
import com.orbenox.erp.product.summary.ProductSummary;
import com.orbenox.erp.unit.Unit;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductPriceListService {
    private final ProductPriceListRepository productPriceListRepository;
    private final ProductPriceListMapper productPriceListMapper;
    private final ProductRepository productRepository;

    public PriceData getPriceDataByProductId(Long productId) {
        ProductSummary product = productRepository.getProductSummaryByProductId(productId);
        List<PriceSummary> priceSummaries = productPriceListRepository.getPriceSummariesByProductId(productId);
        Map<String, List<PriceSummary>> priceMap = priceSummaries.stream()
                .sorted(Comparator.comparing(PriceSummary::getPriceKey))
                .collect(Collectors.groupingBy(PriceSummary::getPriceKey, Collectors.toList()));
        PriceData priceData = new PriceData();
        priceData.setProduct(product);
        priceData.setPriceMap(priceMap);
        return priceData;
    }

    public ProductPriceListDto findDefaultPriceByProduct(Long productId, Long priceListId, Long unitId) {
        ProductPriceList entity = productPriceListRepository.findByProductIdAndPriceListIdAndUnitId(
                productId,
                priceListId,
                unitId);
        return productPriceListMapper.toDto(entity);
    }

    @Transactional
    public PriceData updateProductPrices(UpdateProductPriceRequest request) {
        List<ProductPriceList> entityListToUpdate = request.getPriceListToUpdate().stream()
                .map(item -> {
                    ProductPriceList entity = productPriceListRepository.findById(item.getId()).orElseThrow();
                    entity.setId(item.getId());
                    entity.setPrice(item.getPrice());
                    entity.setFactorToParent(item.getFactorToParent());
                    entity.setFixedPrice(item.getFixedPrice());
                    return entity;
                }).toList();
        productPriceListRepository.saveAll(entityListToUpdate);
        List<ProductPriceList> entityListToInsert = request.getPriceListToInsert().stream()
                .map(item -> {
                    Product product =  new  Product();
                    product.setId(request.getProduct().getId());
                    PriceList priceList = new PriceList();
                    priceList.setId(item.getPriceList().getId());
                    Unit unit = new Unit();
                    unit.setId(item.getUnit().getId());
                    ProductPriceList entity = new ProductPriceList();
                    entity.setId(item.getId());
                    entity.setProduct(product);
                    entity.setUnit(unit);
                    entity.setPriceList(priceList);
                    entity.setPrice(item.getPrice());
                    entity.setFactorToParent(item.getFactorToParent());
                    entity.setFixedPrice(item.getFixedPrice());
                    return entity;
                }).toList();
        productPriceListRepository.saveAll(entityListToInsert);
        List<PriceSummary> priceSummaries = productPriceListRepository.getPriceSummariesByProductId(request.getProduct().getId());
        Map<String, List<PriceSummary>> priceMap = priceSummaries.stream()
                .sorted(Comparator.comparing(PriceSummary::getPriceKey))
                .collect(Collectors.groupingBy(PriceSummary::getPriceKey, Collectors.toList()));
        PriceData priceData = new PriceData();
        ProductSummary product = productRepository.getProductSummaryByProductId(request.getProduct().getId());
        priceData.setProduct(product);
        priceData.setPriceMap(priceMap);
        return priceData;
    }
}
