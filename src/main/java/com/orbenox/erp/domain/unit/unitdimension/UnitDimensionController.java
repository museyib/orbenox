package com.orbenox.erp.domain.unit.unitdimension;

import com.orbenox.erp.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/unitDimensions")
public class UnitDimensionController {
    private final UnitDimensionService unitService;

    @GetMapping
    public ResponseEntity<Response<List<UnitDimensionItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(unitService.getAllItems()));
    }
}
