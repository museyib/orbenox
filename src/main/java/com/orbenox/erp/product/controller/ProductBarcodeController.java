package com.orbenox.erp.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.product.dto.ProductBarcodeData;
import com.orbenox.erp.product.service.ProductBarcodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productBarcodes")
public class ProductBarcodeController {
    private final ProductBarcodeService productBarcodeService;

    @GetMapping("/{productId}")
    public ResponseEntity<Response<ProductBarcodeData>> getProductBarcodesByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(Response.successData(productBarcodeService.getItemsByProductId(productId)));
    }
}
