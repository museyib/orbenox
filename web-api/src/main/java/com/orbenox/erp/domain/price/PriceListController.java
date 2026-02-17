package com.orbenox.erp.domain.price;

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
@RequestMapping("/api/priceLists")
public class PriceListController {
    private final PriceListService priceListService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('PRICE_LIST', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<PriceListItem>>> getAll(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "") String search) {
        Slice<PriceListItem> items = priceListService.getAllItems(page, size, search);
        Map<String, Object> headers = Map.of("hasNext", items.hasNext(), "hasPrev", items.hasPrevious());
        return ResponseEntity.ok(Response.successDataWithHeaders(items.getContent(), headers));
    }

    @GetMapping("/getExcluded/{id}")
    public ResponseEntity<Response<List<PriceListItem.PriceListParent>>> getAllExcluded(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(priceListService.findAllExcluded(id)));
    }

    @PreAuthorize("hasPermission('PRICE_LIST', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<PriceListItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(priceListService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('PRICE_LIST', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<PriceListItem>> create(@Valid @RequestBody PriceListCreateDto dto) {
        return ResponseEntity.ok(Response.successData(priceListService.create(dto)));
    }

    @PreAuthorize("hasPermission('PRICE_LIST', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<PriceListItem>> update(@PathVariable Long id,
                                                          @Valid @RequestBody PriceListUpdateDto dto) {
        return ResponseEntity.ok(Response.successData(priceListService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('PRICE_LIST', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        priceListService.softDelete(id);
        var text = i18n.msg("priceList.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "priceList.deleted"));
    }
}


