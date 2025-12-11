package com.orbenox.erp.product.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductPricesDto {
    private Long productId;
    private List<ProductPriceListDto> prices;
}
