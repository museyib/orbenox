package com.orbenox.erp.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.ProductCategoryDto;
import com.orbenox.erp.product.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productCategories")
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;
    private final LocalizationService i18n;

    @GetMapping
    public ResponseEntity<Response<List<ProductCategoryDto>>> getAll() {
        return ResponseEntity.ok(Response.successData(productCategoryService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductCategoryDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(productCategoryService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<Response<ProductCategoryDto>> create(@RequestBody ProductCategoryDto dto) {
        return ResponseEntity.ok(Response.successData(productCategoryService.create(dto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response<ProductCategoryDto>> update(@PathVariable Long id,
                                                        @RequestBody ProductCategoryDto dto) {
        return ResponseEntity.ok(Response.successData(productCategoryService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        productCategoryService.softDelete(id);
        var text = i18n.msg("productCategory.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "productCategory.deleted"));
    }
}
