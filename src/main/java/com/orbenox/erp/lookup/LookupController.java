package com.orbenox.erp.lookup;

import com.orbenox.erp.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lookups")
@RequiredArgsConstructor
public class LookupController {
    private final LookupService lookupService;

    @PreAuthorize("hasPermission('LOOKUP', 'READ')")
    @GetMapping
    public ResponseEntity<Response<Map<String, Object>>> getLookups(@RequestParam List<String> types) {
        return ResponseEntity.ok(Response.successData(lookupService.getLookups(types)));
    }
}
