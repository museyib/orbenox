package com.orbenox.erp.domain.product.request;

import com.orbenox.erp.domain.product.dto.ProductPriceDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProductPriceRequest {
    private Long productId;
    private List<ProductPriceDto> priceListToUpdate;
    private List<ProductPriceDto> priceListToInsert;
}
