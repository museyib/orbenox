package com.orbenox.erp.domain.resource;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('RESOURCE', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<ResourceItem>>> getActions() {
        return ResponseEntity.ok(Response.successData(resourceService.getAllItems()));
    }

    @PreAuthorize("hasPermission('RESOURCE', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<ResourceData>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(resourceService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('RESOURCE', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<ResourceItem>> create(@Valid @RequestBody ResourceCreateDto dto) {
        return ResponseEntity.ok(Response.successData(resourceService.create(dto)));
    }

    @PreAuthorize("hasPermission('RESOURCE', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<ResourceItem>> update(@PathVariable Long id,
                                                         @Valid @RequestBody ResourceUpdateDto dto) {
        return ResponseEntity.ok(Response.successData(resourceService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('RESOURCE', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        resourceService.softDelete(id);
        var text = i18n.msg("resource.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "resource.deleted"));
    }
}
