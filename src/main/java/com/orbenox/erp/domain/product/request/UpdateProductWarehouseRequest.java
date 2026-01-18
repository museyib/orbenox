package com.orbenox.erp.domain.product.request;

import com.orbenox.erp.domain.product.dto.ProductWarehouseCreateDto;
import com.orbenox.erp.domain.product.dto.ProductWarehouseDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProductWarehouseRequest {
    private Long productId;
    private List<ProductWarehouseDto> warehousesToUpdate;
    private List<ProductWarehouseCreateDto> warehousesToInsert;
    private List<ProductWarehouseDto> warehousesToDelete;
}
