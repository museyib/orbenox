package com.orbenox.erp.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.product.dto.ProductProperties;
import com.orbenox.erp.product.service.ProductPropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productProperties")
@RequiredArgsConstructor
public class ProductPropertyController {
    private final ProductPropertyService productPropertyService;

    @GetMapping
    public ResponseEntity<Response<ProductProperties>> getProductProperties() {
        return ResponseEntity.ok(Response.successData(productPropertyService.getProductProperties()));
    }
}
