package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.domain.product.dto.BrandCreateDto;
import com.orbenox.erp.domain.product.dto.BrandUpdateDto;
import com.orbenox.erp.domain.product.projection.BrandItem;
import com.orbenox.erp.domain.product.service.BrandService;
import com.orbenox.erp.localization.LocalizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/brands")
public class BrandController {
    private final BrandService brandService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('BRAND', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<BrandItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(brandService.getAllItems()));
    }

    @PreAuthorize("hasPermission('BRAND', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<BrandItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(brandService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('BRAND', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<BrandItem>> create(@Valid @RequestBody BrandCreateDto dto) {
        return ResponseEntity.ok(Response.successData(brandService.create(dto)));
    }

    @PreAuthorize("hasPermission('BRAND', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<BrandItem>> update(@PathVariable Long id,
                                                      @Valid @RequestBody BrandUpdateDto dto) {
        return ResponseEntity.ok(Response.successData(brandService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('BRAND', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        brandService.softDelete(id);
        var text = i18n.msg("brand.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "brand.deleted"));
    }
}
