package com.orbenox.erp.common.resource;

import com.orbenox.erp.common.Response;
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

    @PreAuthorize("hasPermission('RESOURCE', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<ResourceDto>>> getActions() {
        return ResponseEntity.ok(Response.successData(resourceService.findAll()));
    }

    @PreAuthorize("hasPermission('RESOURCE', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<ResourceDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(resourceService.findById(id)));
    }

    @PreAuthorize("hasPermission('RESOURCE', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<ResourceDto>> create(@RequestBody ResourceDto resourceDto) {
        return ResponseEntity.ok(Response.successData(resourceService.create(resourceDto)));
    }

    @PreAuthorize("hasPermission('RESOURCE', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<ResourceDto>> update(@PathVariable Long id, @RequestBody ResourceDto resourceDto) {
        return ResponseEntity.ok(Response.successData(resourceService.update(id, resourceDto)));
    }

    @PreAuthorize("hasPermission('RESOURCE', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> deleted(@PathVariable Long id) {
        resourceService.delete(id);
        return ResponseEntity.ok(Response.successMessage("Action deleted"));
    }
}
