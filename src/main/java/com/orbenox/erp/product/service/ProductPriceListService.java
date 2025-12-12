package com.orbenox.erp.product.service;

import com.orbenox.erp.product.dto.ProductPriceListDto;
import com.orbenox.erp.product.dto.ProductPricesDto;
import com.orbenox.erp.product.entity.ProductPriceList;
import com.orbenox.erp.product.mapper.ProductPriceListMapper;
import com.orbenox.erp.product.repository.ProductPriceListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductPriceListService {
    private final ProductPriceListRepository productPriceListRepository;
    private final ProductPriceListMapper productPriceListMapper;

    public ProductPricesDto findAllByProductId(Long productId) {
        List<ProductPriceList> productPriceLists = productPriceListRepository.findAllByProductId(productId);
        List<ProductPriceListDto> dtoList = new ArrayList<>();
        productPriceLists.forEach(productPriceList -> dtoList.add(productPriceListMapper.toDto(productPriceList)));
        ProductPricesDto productPricesDto = new ProductPricesDto();
        productPricesDto.setProductId(productId);
        productPricesDto.setPrices(dtoList);
        return productPricesDto;
    }

    public ProductPriceListDto findDefaultPriceByProduct(Long productId, Long priceListId, Long unitId) {
        ProductPriceList entity = productPriceListRepository.findByProductIdAndPriceListIdAndUnitId(
                productId,
                priceListId,
                unitId);
        return productPriceListMapper.toDto(entity);
    }

    public ProductPricesDto create(ProductPricesDto dto) {
        List<ProductPriceList> entityList = productPriceListMapper.toEntityList(dto.getPrices());
        List<ProductPriceList> saved = productPriceListRepository.saveAll(entityList);
        ProductPricesDto savedDto = new ProductPricesDto();
        savedDto.setProductId(dto.getProductId());
        savedDto.setPrices(productPriceListMapper.toDtoList(saved));
        return savedDto;
    }
}
