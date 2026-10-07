package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.domain.product.projection.ProductUnitData;
import com.orbenox.erp.domain.product.request.UpdateProductUnitRequest;
import com.orbenox.erp.domain.product.service.ProductUnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productUnits")
public class ProductUnitController {
    private final ProductUnitService productUnitService;

    @PreAuthorize("hasPermission('PRODUCT_UNIT', 'READ') or hasPermission('PRODUCT', 'READ')")
    @GetMapping("/{productId}")
    public ResponseEntity<Response<ProductUnitData>> getItemsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(Response.successData(productUnitService.getItemsByProductId(productId)));
    }

    @PreAuthorize("hasPermission('PRODUCT_UNIT', 'UPDATE')")
    @PostMapping
    public ResponseEntity<Response<ProductUnitData>> updateProductUnits(@Valid @RequestBody UpdateProductUnitRequest request) {
        return ResponseEntity.ok(Response.successData(productUnitService.updateProductUnits(request)));
    }
}
