package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.domain.product.projection.ProductPriceItem;
import com.orbenox.erp.domain.product.projection.ProductPricingData;
import com.orbenox.erp.domain.product.request.UpdateProductPriceRequest;
import com.orbenox.erp.domain.product.service.ProductPriceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productPrices")
public class ProductPriceController {
    private final ProductPriceService productPriceService;

    @PreAuthorize("hasPermission('PRODUCT_PRICE', 'READ')")
    @GetMapping("/{productId}")
    public ResponseEntity<Response<ProductPricingData>> getAllByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(Response.successData(productPriceService.getPriceDataByProductId(productId)));
    }

    @PreAuthorize("hasPermission('PRODUCT_PRICE', 'UPDATE')")
    @PostMapping
    public ResponseEntity<Response<ProductPricingData>> updateProductPrices(@Valid @RequestBody UpdateProductPriceRequest request) {
        return ResponseEntity.ok(Response.successData(productPriceService.updateProductPrices(request)));
    }

    @GetMapping("/byPriceListId")
    public ResponseEntity<Response<ProductPriceItem>> getByProductIdAndPriceListId(@RequestParam Long productId,
                                                                                   @RequestParam Long priceListId,
                                                                                   @RequestParam Long unitId) {
        return ResponseEntity.ok(Response.successData(productPriceService.getByProductIdAndPriceListId(productId, priceListId, unitId)));
    }
}
