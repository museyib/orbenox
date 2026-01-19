package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.domain.product.dto.ProductGroupCreateDto;
import com.orbenox.erp.domain.product.dto.ProductGroupUpdateDto;
import com.orbenox.erp.domain.product.projection.ProductGroupItem;
import com.orbenox.erp.domain.product.projection.SimpleProductGroupItem;
import com.orbenox.erp.domain.product.service.ProductGroupService;
import com.orbenox.erp.localization.LocalizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productGroups")
public class ProductGroupController {
    private final ProductGroupService productGroupService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('PRODUCT_GROUP', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<ProductGroupItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(productGroupService.getAllItems()));
    }

    @GetMapping("/getExcluded/{id}")
    public ResponseEntity<Response<List<SimpleProductGroupItem>>> getAllExcluded(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(productGroupService.findAllExcluded(id)));
    }

    @PreAuthorize("hasPermission('PRODUCT_GROUP', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductGroupItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(productGroupService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('PRODUCT_GROUP', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<ProductGroupItem>> create(@Valid @RequestBody ProductGroupCreateDto dto) {
        return ResponseEntity.ok(Response.successData(productGroupService.create(dto)));
    }

    @PreAuthorize("hasPermission('PRODUCT_GROUP', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<ProductGroupItem>> update(@PathVariable Long id,
                                                             @Valid @RequestBody ProductGroupUpdateDto dto) {
        return ResponseEntity.ok(Response.successData(productGroupService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('PRODUCT_GROUP', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        productGroupService.softDelete(id);
        var text = i18n.msg("productGroup.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "productGroup.deleted"));
    }
}
