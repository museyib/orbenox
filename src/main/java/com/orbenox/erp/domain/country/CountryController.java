package com.orbenox.erp.domain.country;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {
    private final CountryService countryService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('COUNTRY', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<CountryItem>>> getActions() {
        return ResponseEntity.ok(Response.successData(countryService.getAllItems()));
    }

    @PreAuthorize("hasPermission('COUNTRY', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<CountryItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(countryService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('COUNTRY', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<CountryItem>> create(@RequestBody CountryDto countryDto) {
        return ResponseEntity.ok(Response.successData(countryService.create(countryDto)));
    }

    @PreAuthorize("hasPermission('COUNTRY', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<CountryItem>> update(@PathVariable Long id, @RequestBody CountryDto countryDto) {
        return ResponseEntity.ok(Response.successData(countryService.update(id, countryDto)));
    }

    @PreAuthorize("hasPermission('COUNTRY', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        countryService.softDelete(id);
        var text = i18n.msg("country.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "country.deleted"));
    }
}
