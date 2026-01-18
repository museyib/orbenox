package com.orbenox.erp.domain.product.request;

import com.orbenox.erp.domain.product.dto.ProductUnitCreateDto;
import com.orbenox.erp.domain.product.dto.ProductUnitDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProductUnitRequest {
    private Long productId;
    private List<ProductUnitDto> unitsToUpdate;
    private List<ProductUnitCreateDto> unitsToInsert;
    private List<ProductUnitDto> unitsToDelete;
}
