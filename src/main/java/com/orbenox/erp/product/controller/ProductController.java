package com.orbenox.erp.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.ProductDto;
import com.orbenox.erp.product.service.ProductService;
import com.orbenox.erp.product.projection.ProductItem;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final LocalizationService i18n;

    @GetMapping
    public ResponseEntity<Response<List<ProductItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(productService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(productService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<Response<ProductDto>> create(@Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok(Response.successData(productService.create(dto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response<ProductDto>> update(@PathVariable Long id,
                                                       @Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok(Response.successData(productService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        productService.softDelete(id);
        var text = i18n.msg("product.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "product.deleted"));
    }
}
