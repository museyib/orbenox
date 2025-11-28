package com.orbenox.erp.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.ProductTypeDto;
import com.orbenox.erp.product.service.ProductTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productTypes")
public class ProductTypeController {
    private final ProductTypeService productTypeService;
    private final LocalizationService i18n;

    @GetMapping
    public ResponseEntity<Response<List<ProductTypeDto>>> getAll() {
        return ResponseEntity.ok(Response.successData(productTypeService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductTypeDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(productTypeService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<Response<ProductTypeDto>> create(@RequestBody ProductTypeDto dto) {
        return ResponseEntity.ok(Response.successData(productTypeService.create(dto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response<ProductTypeDto>> update(@PathVariable Long id,
                                                        @RequestBody ProductTypeDto dto) {
        return ResponseEntity.ok(Response.successData(productTypeService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        productTypeService.softDelete(id);
        var text = i18n.msg("productType.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "productType.deleted"));
    }
}
