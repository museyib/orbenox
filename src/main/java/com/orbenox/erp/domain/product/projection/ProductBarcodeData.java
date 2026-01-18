package com.orbenox.erp.domain.product.projection;

import lombok.Data;

import java.util.List;

@Data
public class ProductBarcodeData {
    private SimpleProductItem product;
    private List<ProductBarcodeItem> barcodes;
}
