package com.orbenox.erp.domain.product.request;

import com.orbenox.erp.domain.product.dto.ProductUnitCreateDto;
import com.orbenox.erp.domain.product.dto.ProductUnitUpdateDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProductUnitRequest {
    private Long productId;
    private List<ProductUnitUpdateDto> unitsToUpdate;
    private List<ProductUnitCreateDto> unitsToInsert;
    private List<ProductUnitUpdateDto> unitsToDelete;
}
