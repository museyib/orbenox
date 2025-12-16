package com.orbenox.erp.price;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/priceLists")
public class PriceListController {
    private final PriceListService priceListService;
    private final LocalizationService i18n;

    @GetMapping
    public ResponseEntity<Response<List<PriceListItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(priceListService.findAll()));
    }

    @GetMapping("/getExcluded/{id}")
    public ResponseEntity<Response<List<PriceListDto>>> getAllExcluded(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(priceListService.findAllExcluded(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<PriceListItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(priceListService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<Response<PriceListDto>> create(@RequestBody PriceListDto dto) {
        return ResponseEntity.ok(Response.successData(priceListService.create(dto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response<PriceListDto>> update(@PathVariable Long id,
                                                         @RequestBody PriceListDto dto) {
        return ResponseEntity.ok(Response.successData(priceListService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        priceListService.softDelete(id);
        var text = i18n.msg("priceList.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "priceList.deleted"));
    }
}
