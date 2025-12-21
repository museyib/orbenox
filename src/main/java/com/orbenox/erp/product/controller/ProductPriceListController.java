package com.orbenox.erp.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.product.dto.ProductPricingData;
import com.orbenox.erp.product.request.UpdateProductPriceRequest;
import com.orbenox.erp.product.service.ProductPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productPriceLists")
public class ProductPriceListController {
    private final ProductPriceService productPriceService;

    @PreAuthorize("hasPermission('PRODUCT_PRICE', 'READ')")
    @GetMapping("/{productId}")
    public ResponseEntity<Response<ProductPricingData>> getAllByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(Response.successData(productPriceService.getPriceDataByProductId(productId)));
    }

    @PreAuthorize("hasPermission('PRODUCT_PRICE', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<ProductPricingData>> create(@RequestBody UpdateProductPriceRequest request) {
        return ResponseEntity.ok(Response.successData(productPriceService.updateProductPrices(request)));
    }
}
