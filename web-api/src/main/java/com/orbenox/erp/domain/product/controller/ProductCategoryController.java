package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.domain.product.dto.ProductCategoryCreateDto;
import com.orbenox.erp.domain.product.dto.ProductCategoryUpdateDto;
import com.orbenox.erp.domain.product.projection.ProductCategoryItem;
import com.orbenox.erp.domain.product.service.ProductCategoryService;
import com.orbenox.erp.localization.LocalizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productCategories")
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('PRODUCT_CATEGORY', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<ProductCategoryItem>>> getAll(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "") String search) {
        Slice<ProductCategoryItem> items = productCategoryService.getAllItems(page, size, search);
        Map<String, Object> headers = Map.of("hasNext", items.hasNext(), "hasPrev", items.hasPrevious());
        return ResponseEntity.ok(Response.successDataWithHeaders(items.getContent(), headers));
    }

    @PreAuthorize("hasPermission('PRODUCT_CATEGORY', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductCategoryItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(productCategoryService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('PRODUCT_CATEGORY', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<ProductCategoryItem>> create(@Valid @RequestBody ProductCategoryCreateDto dto) {
        return ResponseEntity.ok(Response.successData(productCategoryService.create(dto)));
    }

    @PreAuthorize("hasPermission('PRODUCT_CATEGORY', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<ProductCategoryItem>> update(@PathVariable Long id,
                                                                @Valid @RequestBody ProductCategoryUpdateDto dto) {
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


