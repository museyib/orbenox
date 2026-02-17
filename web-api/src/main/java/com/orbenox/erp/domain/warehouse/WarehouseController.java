package com.orbenox.erp.domain.warehouse;

import com.orbenox.erp.common.Response;
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
@RequestMapping("/api/warehouses")
public class WarehouseController {
    private final WarehouseService warehouseService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('WAREHOUSE', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<WarehouseItem>>> getAll(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "") String search) {
        Slice<WarehouseItem> items = warehouseService.getAllItems(page, size, search);
        Map<String, Object> headers = Map.of("hasNext", items.hasNext(), "hasPrev", items.hasPrevious());
        return ResponseEntity.ok(Response.successDataWithHeaders(items.getContent(), headers));
    }

    @PreAuthorize("hasPermission('WAREHOUSE', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<WarehouseItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(warehouseService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('WAREHOUSE', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<WarehouseItem>> create(@Valid @RequestBody WarehouseCreateDto dto) {
        return ResponseEntity.ok(Response.successData(warehouseService.create(dto)));
    }

    @PreAuthorize("hasPermission('WAREHOUSE', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<WarehouseItem>> update(@PathVariable Long id,
                                                         @Valid @RequestBody WarehouseUpdateDto dto) {
        return ResponseEntity.ok(Response.successData(warehouseService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('WAREHOUSE', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        warehouseService.softDelete(id);
        var text = i18n.msg("warehouse.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "warehouse.deleted"));
    }
}


