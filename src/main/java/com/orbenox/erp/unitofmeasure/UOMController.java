package com.orbenox.erp.unitofmeasure;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/uom")
public class UOMController {
    private final UOMService uomService;
    private final LocalizationService i18n;

    @GetMapping
    public ResponseEntity<Response<List<UnitOfMeasureDto>>> getAll() {
        return ResponseEntity.ok(Response.successData(uomService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<UnitOfMeasureDto>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(uomService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<Response<UnitOfMeasureDto>> create(@RequestBody UnitOfMeasureDto uomDto) {
        return ResponseEntity.ok(Response.successData(uomService.create(uomDto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response<UnitOfMeasureDto>> update(@PathVariable Long id,
                                                             @RequestBody UnitOfMeasureDto uomDto) {
        return ResponseEntity.ok(Response.successData(uomService.update(id, uomDto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        uomService.deleteById(id);
        var text = i18n.msg("uom.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "uom deleted"));
    }
}
