package com.orbenox.erp.domain.product.request;

import com.orbenox.erp.domain.product.dto.ProductWarehouseCreateDto;
import com.orbenox.erp.domain.product.dto.ProductWarehouseUpdateDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProductWarehouseRequest {
    private Long productId;
    private List<ProductWarehouseUpdateDto> warehousesToUpdate;
    private List<ProductWarehouseCreateDto> warehousesToInsert;
    private List<ProductWarehouseUpdateDto> warehousesToDelete;
}
