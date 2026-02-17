package com.orbenox.erp.domain.businesspartner;

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
@RequestMapping("/api/businessPartners")
@RequiredArgsConstructor
public class BusinessPartnerController {
    private final BusinessPartnerService businessPartnerService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('BUSINESS_PARTNER', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<BusinessPartnerItem>>> getAll(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "") String search) {
        Slice<BusinessPartnerItem> items = businessPartnerService.getAllItems(page, size, search);
        Map<String, Object> headers = Map.of("hasNext", items.hasNext(), "hasPrev", items.hasPrevious());
        return ResponseEntity.ok(Response.successDataWithHeaders(items.getContent(), headers));
    }

    @PreAuthorize("hasPermission('BUSINESS_PARTNER', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<BusinessPartnerItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(businessPartnerService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('BUSINESS_PARTNER', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<BusinessPartnerItem>> create(@Valid @RequestBody BusinessPartnerCreateDto dto) {
        return ResponseEntity.ok(Response.successData(businessPartnerService.create(dto)));
    }

    @PreAuthorize("hasPermission('BUSINESS_PARTNER', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<BusinessPartnerItem>> update(@PathVariable Long id,
                                                                @Valid @RequestBody BusinessPartnerUpdateDto dto) {
        return ResponseEntity.ok(Response.successData(businessPartnerService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('BUSINESS_PARTNER', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        businessPartnerService.softDelete(id);
        var text = i18n.msg("businessPartner.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "businessPartner.deleted"));
    }
}


