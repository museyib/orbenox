package com.orbenox.erp.domain.currency;

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
@RequestMapping("/api/currencies")
public class CurrencyController {
    private final CurrencyService currencyService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('CURRENCY', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<CurrencyItem>>> getAll(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "") String search) {
        Slice<CurrencyItem> items = currencyService.getAllItems(page, size, search);
        Map<String, Object> headers = Map.of("hasNext", items.hasNext(), "hasPrev", items.hasPrevious());
        return ResponseEntity.ok(Response.successDataWithHeaders(items.getContent(), headers));
    }

    @PreAuthorize("hasPermission('CURRENCY', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<CurrencyItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(currencyService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('CURRENCY', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<CurrencyItem>> create(@Valid @RequestBody CurrencyCreateDto dto) {
        return ResponseEntity.ok(Response.successData(currencyService.create(dto)));
    }

    @PreAuthorize("hasPermission('CURRENCY', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<CurrencyItem>> update(@PathVariable Long id,
                                                         @Valid @RequestBody CurrencyUpdateDto dto) {
        return ResponseEntity.ok(Response.successData(currencyService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('CURRENCY', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        currencyService.softDelete(id);
        var text = i18n.msg("currency.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "currency.deleted"));
    }
}


