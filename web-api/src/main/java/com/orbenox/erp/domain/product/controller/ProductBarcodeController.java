package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.domain.product.projection.ProductBarcodeData;
import com.orbenox.erp.domain.product.request.UpdateProductBarcodeRequest;
import com.orbenox.erp.domain.product.service.ProductBarcodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productBarcodes")
public class ProductBarcodeController {
    private final ProductBarcodeService productBarcodeService;

    @PreAuthorize("hasPermission('PRODUCT_BARCODE', 'READ') or hasPermission('PRODUCT', 'READ')")
    @GetMapping("/{productId}")
    public ResponseEntity<Response<ProductBarcodeData>> getProductBarcodesByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(Response.successData(productBarcodeService.getItemsByProductId(productId)));
    }

    @PreAuthorize("hasPermission('PRODUCT_BARCODE', 'UPDATE')")
    @PostMapping
    public ResponseEntity<Response<ProductBarcodeData>> updateProductBarcodes(@Valid @RequestBody UpdateProductBarcodeRequest request) {
        return ResponseEntity.ok(Response.successData(productBarcodeService.updateBarcodes(request)));
    }
}
