package com.orbenox.erp.currency;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/currencies")
public class CurrencyController {
    private final CurrencyService currencyService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('CURRENCY', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<CurrencyItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(currencyService.getAllItems()));
    }

    @PreAuthorize("hasPermission('CURRENCY', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<CurrencyItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(currencyService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('CURRENCY', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<CurrencyItem>> create(@RequestBody CurrencyDto dto) {
        return ResponseEntity.ok(Response.successData(currencyService.create(dto)));
    }

    @PreAuthorize("hasPermission('CURRENCY', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<CurrencyItem>> update(@PathVariable Long id,
                                                        @RequestBody CurrencyDto dto) {
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
