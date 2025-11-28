package com.orbenox.erp.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.ProductGroupDto;
import com.orbenox.erp.product.service.ProductGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productGroups")
public class ProductGroupController {
    private final ProductGroupService productGroupService;
    private final LocalizationService i18n;

    @GetMapping
    public ResponseEntity<Response<List<ProductGroupDto>>> getAll() {
        return ResponseEntity.ok(Response.successData(productGroupService.findAll()));
    }

    @GetMapping("/getExcluded/{id}")
    public ResponseEntity<Response<List<ProductGroupDto>>> getAllExcluded(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(productGroupService.findAllExcluded(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductGroupDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(productGroupService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<Response<ProductGroupDto>> create(@RequestBody ProductGroupDto dto) {
        return ResponseEntity.ok(Response.successData(productGroupService.create(dto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response<ProductGroupDto>> update(@PathVariable Long id,
                                                        @RequestBody ProductGroupDto dto) {
        return ResponseEntity.ok(Response.successData(productGroupService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        productGroupService.softDelete(id);
        var text = i18n.msg("productGroup.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "productGroup.deleted"));
    }
}
