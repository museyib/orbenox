package com.orbenox.erp.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.PriceDto;
import com.orbenox.erp.product.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/prices")
public class PriceController {
    private final PriceService priceService;
    private final LocalizationService i18n;

    @GetMapping
    public ResponseEntity<Response<List<PriceDto>>> getAll() {
        return ResponseEntity.ok(Response.successData(priceService.findAll()));
    }

    @GetMapping("/getExcluded/{id}")
    public ResponseEntity<Response<List<PriceDto>>> getAllExcluded(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(priceService.findAllExcluded(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<PriceDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(priceService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<Response<PriceDto>> create(@RequestBody PriceDto dto) {
        return ResponseEntity.ok(Response.successData(priceService.create(dto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response<PriceDto>> update(@PathVariable Long id,
                                                        @RequestBody PriceDto dto) {
        return ResponseEntity.ok(Response.successData(priceService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        priceService.softDelete(id);
        var text = i18n.msg("price.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "price.deleted"));
    }
}
