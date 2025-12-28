package com.orbenox.erp.product.dto;

import com.orbenox.erp.product.projection.ProductBarcodeItem;
import com.orbenox.erp.product.projection.SimpleProductItem;
import lombok.Data;

import java.util.List;

@Data
public class ProductBarcodeData {
    private SimpleProductItem product;
    private List<ProductBarcodeItem> barcodes;
}
