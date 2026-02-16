package com.orbenox.erp.domain.postingrule;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/postingRules")
@RequiredArgsConstructor
public class PostingRuleController {
    private final PostingRuleService postingRuleService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('POSTING_RULE', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<PostingRuleItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(postingRuleService.getAllItems()));
    }

    @PreAuthorize("hasPermission('POSTING_RULE', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<PostingRuleItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(postingRuleService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('POSTING_RULE', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<PostingRuleItem>> create(@Valid @RequestBody PostingRuleDto dto) {
        return ResponseEntity.ok(Response.successData(postingRuleService.create(dto)));
    }

    @PreAuthorize("hasPermission('POSTING_RULE', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<PostingRuleItem>> update(@PathVariable Long id,
                                                            @Valid @RequestBody PostingRuleDto dto) {
        return ResponseEntity.ok(Response.successData(postingRuleService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('POSTING_RULE', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        postingRuleService.delete(id);
        var text = i18n.msg("postingRule.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "postingRule.deleted"));
    }
}
