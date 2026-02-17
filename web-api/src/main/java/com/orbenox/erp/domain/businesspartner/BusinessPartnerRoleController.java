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
@RequestMapping("/api/businessPartnerRoles")
@RequiredArgsConstructor
public class BusinessPartnerRoleController {
    private final BusinessPartnerRoleService businessPartnerRoleService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('BUSINESS_PARTNER_ROLE', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<BusinessPartnerRoleItem>>> getAll(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "") String search) {
        Slice<BusinessPartnerRoleItem> items = businessPartnerRoleService.getAllItems(page, size, search);
        Map<String, Object> headers = Map.of("hasNext", items.hasNext(), "hasPrev", items.hasPrevious());
        return ResponseEntity.ok(Response.successDataWithHeaders(items.getContent(), headers));
    }

    @PreAuthorize("hasPermission('BUSINESS_PARTNER_ROLE', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<BusinessPartnerRoleItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(businessPartnerRoleService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('BUSINESS_PARTNER_ROLE', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<BusinessPartnerRoleItem>> create(@Valid @RequestBody BusinessPartnerRoleCreateDto dto) {
        return ResponseEntity.ok(Response.successData(businessPartnerRoleService.create(dto)));
    }

    @PreAuthorize("hasPermission('BUSINESS_PARTNER_ROLE', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<BusinessPartnerRoleItem>> update(@PathVariable Long id,
                                                                    @Valid @RequestBody BusinessPartnerRoleUpdateDto dto) {
        return ResponseEntity.ok(Response.successData(businessPartnerRoleService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('BUSINESS_PARTNER_ROLE', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        businessPartnerRoleService.softDelete(id);
        var text = i18n.msg("businessPartnerRole.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "businessPartnerRole.deleted"));
    }
}


