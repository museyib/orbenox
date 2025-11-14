package com.orbenox.erp.unit;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/units")
public class UnitController {
    private final UnitService unitService;
    private final LocalizationService i18n;

    @GetMapping
    public ResponseEntity<Response<List<UnitDto>>> getAll() {
        return ResponseEntity.ok(Response.successData(unitService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<UnitDto>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(unitService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<Response<UnitDto>> create(@RequestBody UnitDto uomDto) {
        return ResponseEntity.ok(Response.successData(unitService.create(uomDto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response<UnitDto>> update(@PathVariable Long id,
                                                    @RequestBody UnitDto uomDto) {
        return ResponseEntity.ok(Response.successData(unitService.update(id, uomDto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        unitService.deleteById(id);
        var text = i18n.msg("uom.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "uom deleted"));
    }
}
