package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.domain.product.projection.ProductWarehouseData;
import com.orbenox.erp.domain.product.request.UpdateProductWarehouseRequest;
import com.orbenox.erp.domain.product.service.ProductWarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productWarehouses")
public class ProductWarehouseController {
    private final ProductWarehouseService productWarehouseService;

    @PreAuthorize("hasPermission('PRODUCT_WAREHOUSE', 'READ') or hasPermission('PRODUCT', 'READ')")
    @GetMapping("/{productId}")
    public ResponseEntity<Response<ProductWarehouseData>> getItemsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(Response.successData(productWarehouseService.getItemsByProductId(productId)));
    }

    @PreAuthorize("hasPermission('PRODUCT_WAREHOUSE', 'UPDATE')")
    @PostMapping
    public ResponseEntity<Response<ProductWarehouseData>> updateProductWarehouses(@Valid @RequestBody UpdateProductWarehouseRequest request) {
        return ResponseEntity.ok(Response.successData(productWarehouseService.updateProductWarehouses(request)));
    }
}
