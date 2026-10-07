package com.orbenox.erp.domain.product.request;

import com.orbenox.erp.domain.product.dto.ProductPriceCreateDto;
import com.orbenox.erp.domain.product.dto.ProductPriceUpdateDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProductPriceRequest {
    private Long productId;
    private List<ProductPriceUpdateDto> priceListToUpdate;
    private List<ProductPriceCreateDto> priceListToInsert;
}
