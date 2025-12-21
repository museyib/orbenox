package com.orbenox.erp.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.ProductDto;
import com.orbenox.erp.product.projection.ProductItem;
import com.orbenox.erp.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('PRODUCT', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<ProductItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(productService.getAllItems()));
    }

    @PreAuthorize("hasPermission('PRODUCT', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(productService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('PRODUCT', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<ProductItem>> create(@Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok(Response.successData(productService.create(dto)));
    }

    @PreAuthorize("hasPermission('PRODUCT', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<ProductItem>> update(@PathVariable Long id,
                                                       @Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok(Response.successData(productService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('PRODUCT', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        productService.softDelete(id);
        var text = i18n.msg("product.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "product.deleted"));
    }
}
