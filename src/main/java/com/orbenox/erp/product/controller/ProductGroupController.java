package com.orbenox.erp.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.ProductGroupDto;
import com.orbenox.erp.product.projection.ProductGroupItem;
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
    public ResponseEntity<Response<List<ProductGroupItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(productGroupService.getAllItems()));
    }

    @GetMapping("/getExcluded/{id}")
    public ResponseEntity<Response<List<ProductGroupItem.Parent>>> getAllExcluded(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(productGroupService.findAllExcluded(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductGroupItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(productGroupService.getItemById(id)));
    }

    @PostMapping
    public ResponseEntity<Response<ProductGroupItem>> create(@RequestBody ProductGroupDto dto) {
        return ResponseEntity.ok(Response.successData(productGroupService.create(dto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response<ProductGroupItem>> update(@PathVariable Long id,
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
