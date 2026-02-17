package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.domain.product.dto.ProductClassCreateDto;
import com.orbenox.erp.domain.product.dto.ProductClassUpdateDto;
import com.orbenox.erp.domain.product.projection.ProductClassItem;
import com.orbenox.erp.domain.product.service.ProductClassService;
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
@RequestMapping("/api/productClasses")
public class ProductClassController {
    private final ProductClassService productClassService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('PRODUCT_CLASS', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<ProductClassItem>>> getAll(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "") String search) {
        Slice<ProductClassItem> items = productClassService.getAllItems(page, size, search);
        Map<String, Object> headers = Map.of("hasNext", items.hasNext(), "hasPrev", items.hasPrevious());
        return ResponseEntity.ok(Response.successDataWithHeaders(items.getContent(), headers));
    }

    @PreAuthorize("hasPermission('PRODUCT_CLASS', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductClassItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(productClassService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('PRODUCT_CLASS', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<ProductClassItem>> create(@Valid @RequestBody ProductClassCreateDto dto) {
        return ResponseEntity.ok(Response.successData(productClassService.create(dto)));
    }

    @PreAuthorize("hasPermission('PRODUCT_CLASS', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<ProductClassItem>> update(@PathVariable Long id,
                                                             @Valid @RequestBody ProductClassUpdateDto dto) {
        return ResponseEntity.ok(Response.successData(productClassService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('PRODUCT_CLASS', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        productClassService.softDelete(id);
        var text = i18n.msg("productClass.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "productClass.deleted"));
    }
}


