package com.orbenox.erp.domain.product.dto;

import com.orbenox.erp.domain.product.projection.ProductBarcodeItem;
import com.orbenox.erp.domain.product.projection.SimpleProductItem;
import lombok.Data;

import java.util.List;

@Data
public class ProductBarcodeData {
    private SimpleProductItem product;
    private List<ProductBarcodeItem> barcodes;
}
