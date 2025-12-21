package com.orbenox.erp.currency;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/currencies")
public class CurrencyController {
    private final CurrencyService currencyService;
    private final LocalizationService i18n;

    @GetMapping
    public ResponseEntity<Response<List<CurrencyItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(currencyService.getAllItems()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<CurrencyItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(currencyService.getItemById(id)));
    }

    @PostMapping
    public ResponseEntity<Response<CurrencyItem>> create(@RequestBody CurrencyDto dto) {
        return ResponseEntity.ok(Response.successData(currencyService.create(dto)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Response<CurrencyItem>> update(@PathVariable Long id,
                                                        @RequestBody CurrencyDto dto) {
        return ResponseEntity.ok(Response.successData(currencyService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        currencyService.softDelete(id);
        var text = i18n.msg("currency.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "currency.deleted"));
    }
}
