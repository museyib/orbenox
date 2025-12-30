package com.orbenox.erp.domain.product.request;

import com.orbenox.erp.domain.product.dto.ProductBarcodeDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProductBarcodeRequest {
    private Long productId;
    private List<ProductBarcodeDto> barcodesToUpdate;
    private List<ProductBarcodeDto> barcodesToInsert;
    private List<ProductBarcodeDto> barcodesToDelete;
}
