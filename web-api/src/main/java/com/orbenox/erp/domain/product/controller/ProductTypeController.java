package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.domain.product.dto.ProductTypeCreateDto;
import com.orbenox.erp.domain.product.dto.ProductTypeUpdateDto;
import com.orbenox.erp.domain.product.projection.ProductTypeItem;
import com.orbenox.erp.domain.product.service.ProductTypeService;
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
@RequestMapping("/api/productTypes")
public class ProductTypeController {
    private final ProductTypeService productTypeService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('PRODUCT_TYPE', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<ProductTypeItem>>> getAll(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "") String search) {
        Slice<ProductTypeItem> items = productTypeService.getAllItems(page, size, search);
        Map<String, Object> headers = Map.of("hasNext", items.hasNext(), "hasPrev", items.hasPrevious());
        return ResponseEntity.ok(Response.successDataWithHeaders(items.getContent(), headers));
    }

    @PreAuthorize("hasPermission('PRODUCT_TYPE', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductTypeItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(productTypeService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('PRODUCT_TYPE', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<ProductTypeItem>> create(@Valid @RequestBody ProductTypeCreateDto dto) {
        return ResponseEntity.ok(Response.successData(productTypeService.create(dto)));
    }

    @PreAuthorize("hasPermission('PRODUCT_TYPE', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<ProductTypeItem>> update(@PathVariable Long id,
                                                            @Valid @RequestBody ProductTypeUpdateDto dto) {
        return ResponseEntity.ok(Response.successData(productTypeService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('PRODUCT_TYPE', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        productTypeService.softDelete(id);
        var text = i18n.msg("productType.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "productType.deleted"));
    }
}


