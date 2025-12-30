package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.domain.product.dto.ProductBarcodeData;
import com.orbenox.erp.domain.product.request.UpdateProductBarcodeRequest;
import com.orbenox.erp.domain.product.service.ProductBarcodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productBarcodes")
public class ProductBarcodeController {
    private final ProductBarcodeService productBarcodeService;

    @GetMapping("/{productId}")
    public ResponseEntity<Response<ProductBarcodeData>> getProductBarcodesByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(Response.successData(productBarcodeService.getItemsByProductId(productId)));
    }

    @PostMapping
    public ResponseEntity<Response<ProductBarcodeData>> updateProductBarcodes(@RequestBody UpdateProductBarcodeRequest request) {
        return ResponseEntity.ok(Response.successData(productBarcodeService.updateBarcodes(request)));
    }
}
