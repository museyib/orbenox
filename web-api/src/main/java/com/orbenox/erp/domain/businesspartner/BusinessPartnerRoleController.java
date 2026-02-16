package com.orbenox.erp.domain.businesspartner;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/businessPartnerRoles")
@RequiredArgsConstructor
public class BusinessPartnerRoleController {
    private final BusinessPartnerRoleService businessPartnerRoleService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('BUSINESS_PARTNER_ROLE', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<BusinessPartnerRoleItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(businessPartnerRoleService.getAllItems()));
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
