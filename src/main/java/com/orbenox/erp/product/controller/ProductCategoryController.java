package com.orbenox.erp.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.ProductCategoryDto;
import com.orbenox.erp.product.projection.ProductCategoryItem;
import com.orbenox.erp.product.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productCategories")
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('PRODUCT_CATEGORY', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<ProductCategoryItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(productCategoryService.getAllItems()));
    }

    @PreAuthorize("hasPermission('PRODUCT_CATEGORY', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductCategoryItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(productCategoryService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('PRODUCT_CATEGORY', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<ProductCategoryItem>> create(@RequestBody ProductCategoryDto dto) {
        return ResponseEntity.ok(Response.successData(productCategoryService.create(dto)));
    }

    @PreAuthorize("hasPermission('PRODUCT_CATEGORY', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<ProductCategoryItem>> update(@PathVariable Long id,
                                                                @RequestBody ProductCategoryDto dto) {
        return ResponseEntity.ok(Response.successData(productCategoryService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('PRODUCT_CATEGORY', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        productCategoryService.softDelete(id);
        var text = i18n.msg("productCategory.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "productCategory.deleted"));
    }
}
