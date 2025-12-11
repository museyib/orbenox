package com.orbenox.erp.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.product.dto.ProductPricesDto;
import com.orbenox.erp.product.service.ProductPriceListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productPriceLists")
public class ProductPriceListController {
    private final ProductPriceListService productPriceListService;

    @GetMapping("/{productId}")
    public ResponseEntity<Response<ProductPricesDto>> findByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(Response.successData(productPriceListService.findAllByProductId(productId)));
    }
}
