package com.orbenox.erp.domain.unit;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/units")
public class UnitController {
    private final UnitService unitService;
    private final UnitConverterService unitConverterService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('UNIT', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<UnitItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(unitService.getAllItems()));
    }

    @PreAuthorize("hasPermission('UNIT', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<UnitItem>> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(unitService.getItemById(id)));
    }

    @GetMapping("/byDimension/{dimensionId}")
    public ResponseEntity<Response<List<UnitItem>>> getAllByDimensionId(@PathVariable Long dimensionId) {
        return ResponseEntity.ok(Response.successData(unitService.findAllByDimensionId(dimensionId)));
    }

    @PreAuthorize("hasPermission('UNIT', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<UnitItem>> create(@Valid @RequestBody UnitCreateDto dto) {
        return ResponseEntity.ok(Response.successData(unitService.create(dto)));
    }

    @PreAuthorize("hasPermission('UNIT', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<UnitItem>> update(@PathVariable Long id,
                                                     @Valid @RequestBody UnitUpdateDto dto) {
        return ResponseEntity.ok(Response.successData(unitService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('UNIT', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        unitService.softDelete(id);
        var text = i18n.msg("unit.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "uom.deleted"));
    }

    @PostMapping("/convert")
    public ResponseEntity<Response<BigDecimal>> convert(@RequestBody UnitConversionRequest request) {
        return ResponseEntity.ok(Response.successData(unitConverterService.convert(
                request.getValue(),
                request.getFrom(),
                request.getTo()
        )));
    }
}
