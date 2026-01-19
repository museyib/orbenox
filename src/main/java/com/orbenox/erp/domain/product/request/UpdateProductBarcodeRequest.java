package com.orbenox.erp.domain.product.request;

import com.orbenox.erp.domain.product.dto.ProductBarcodeCreateDto;
import com.orbenox.erp.domain.product.dto.ProductBarcodeUpdateDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProductBarcodeRequest {
    private Long productId;
    private List<ProductBarcodeUpdateDto> barcodesToUpdate;
    private List<ProductBarcodeCreateDto> barcodesToInsert;
    private List<ProductBarcodeUpdateDto> barcodesToDelete;
}
