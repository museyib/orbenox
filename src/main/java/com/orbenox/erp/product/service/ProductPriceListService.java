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
}
