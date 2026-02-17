package com.orbenox.erp.domain.unit.unitdimension;

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
@RequestMapping("/api/unitDimensions")
public class UnitDimensionController {
    private final UnitDimensionService unitDimensionService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('UNIT_DIMENSION', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<UnitDimensionItem>>> getAll(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "") String search) {
        Slice<UnitDimensionItem> items = unitDimensionService.getAllItems(page, size, search);
        Map<String, Object> headers = Map.of("hasNext", items.hasNext(), "hasPrev", items.hasPrevious());
        return ResponseEntity.ok(Response.successDataWithHeaders(items.getContent(), headers));
    }

    @PreAuthorize("hasPermission('UNIT_DIMENSION', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<UnitDimensionItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(unitDimensionService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('UNIT_DIMENSION', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<UnitDimensionItem>> create(@Valid @RequestBody UnitDimensionDto dto) {
        return ResponseEntity.ok(Response.successData(unitDimensionService.create(dto)));
    }

    @PreAuthorize("hasPermission('UNIT_DIMENSION', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<UnitDimensionItem>> update(@PathVariable Long id,
                                                              @Valid @RequestBody UnitDimensionDto dto) {
        return ResponseEntity.ok(Response.successData(unitDimensionService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('UNIT_DIMENSION', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        unitDimensionService.softDelete(id);
        var text = i18n.msg("unitDimension.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "unitDimension.deleted"));
    }
}


