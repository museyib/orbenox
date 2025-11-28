package com.orbenox.erp.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.product.dto.ProducerDto;
import com.orbenox.erp.product.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/producers")
public class ProducerController {
    private final ProducerService producerService;
    private final LocalizationService i18n;

    @GetMapping
    public ResponseEntity<Response<List<ProducerDto>>> getAll() {
        return ResponseEntity.ok(Response.successData(producerService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ProducerDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(producerService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<Response<ProducerDto>> create(@RequestBody ProducerDto dto) {
        return ResponseEntity.ok(Response.successData(producerService.create(dto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response<ProducerDto>> update(@PathVariable Long id,
                                                        @RequestBody ProducerDto dto) {
        return ResponseEntity.ok(Response.successData(producerService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        producerService.softDelete(id);
        var text = i18n.msg("producer.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "producer.deleted"));
    }
}
