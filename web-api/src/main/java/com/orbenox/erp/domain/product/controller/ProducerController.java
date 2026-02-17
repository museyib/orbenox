package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.domain.product.dto.ProducerCreateDto;
import com.orbenox.erp.domain.product.dto.ProducerUpdateDto;
import com.orbenox.erp.domain.product.projection.ProducerItem;
import com.orbenox.erp.domain.product.service.ProducerService;
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
@RequestMapping("/api/producers")
public class ProducerController {
    private final ProducerService producerService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('PRODUCER', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<ProducerItem>>> getAll(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "") String search) {
        Slice<ProducerItem> items = producerService.getAllItems(page, size, search);
        Map<String, Object> headers = Map.of("hasNext", items.hasNext(), "hasPrev", items.hasPrevious());
        return ResponseEntity.ok(Response.successDataWithHeaders(items.getContent(), headers));
    }

    @PreAuthorize("hasPermission('PRODUCER', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<ProducerItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(producerService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('PRODUCER', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<ProducerItem>> create(@Valid @RequestBody ProducerCreateDto dto) {
        return ResponseEntity.ok(Response.successData(producerService.create(dto)));
    }

    @PreAuthorize("hasPermission('PRODUCER', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<ProducerItem>> update(@PathVariable Long id,
                                                         @Valid @RequestBody ProducerUpdateDto dto) {
        return ResponseEntity.ok(Response.successData(producerService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('PRODUCER', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        producerService.softDelete(id);
        var text = i18n.msg("producer.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "producer.deleted"));
    }
}


