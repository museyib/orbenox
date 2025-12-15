package com.orbenox.erp.product.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdateProductPriceRequest {
    private ProductRequest product;
    private List<ProductPriceRequest> priceListToUpdate;
    private List<ProductPriceRequest> priceListToInsert;
}
