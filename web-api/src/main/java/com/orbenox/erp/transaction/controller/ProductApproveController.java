package com.orbenox.erp.transaction.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.transaction.idempotency.IdempotencyService;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.transaction.command.CreateProductApproveCommand;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.projection.DocumentItem;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import com.orbenox.erp.transaction.service.ProductApproveActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productApproves")
@RequiredArgsConstructor
public class ProductApproveController {
    private final ProductApproveActionService documentActionService;
    private final DocumentRepository documentRepository;
    private final LocalizationService i18n;
    private final IdempotencyService idempotencyService;

    @PreAuthorize("hasPermission('PRODUCT_APPROVE', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<DocumentItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(documentRepository.getItemsByType(1L)));
    }

    @PreAuthorize("hasPermission('PRODUCT_APPROVE', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<DocumentItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(getItemOrThrow(id)));
    }

    @PreAuthorize("hasPermission('PRODUCT_APPROVE', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<DocumentItem>> create(@RequestHeader("Idempotency-Key") String key,
                                                         @RequestBody CreateProductApproveCommand command) {
        Document document = documentActionService.createDraft(command);
        DocumentItem item = getItemOrThrow(document.getId());
        Response<DocumentItem> response = Response.successData(item);
        idempotencyService.complete(key, response, 200);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasPermission('PRODUCT_APPROVE', 'SUBMIT')")
    @PostMapping("/{id}/submit")
    public ResponseEntity<Response<DocumentItem>> submit(@PathVariable Long id) {
        documentActionService.submit(id);
        return ResponseEntity.ok(Response.successData(getItemOrThrow(id)));
    }

    @PreAuthorize("hasPermission('PRODUCT_APPROVE', 'APPROVE')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<Response<DocumentItem>> approve(@PathVariable Long id) {
        documentActionService.approve(id);
        return ResponseEntity.ok(Response.successData(getItemOrThrow(id)));
    }

    @PreAuthorize("hasPermission('PRODUCT_APPROVE', 'POST')")
    @PostMapping("/{id}/post")
    public ResponseEntity<Response<DocumentItem>> post(@PathVariable Long id) {
        documentActionService.post(id);
        return ResponseEntity.ok(Response.successData(getItemOrThrow(id)));
    }

    @PreAuthorize("hasPermission('PRODUCT_APPROVE', 'REJECT')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<Response<DocumentItem>> reject(@PathVariable Long id) {
        documentActionService.reject(id);
        return ResponseEntity.ok(Response.successData(getItemOrThrow(id)));
    }

    @PreAuthorize("hasPermission('PRODUCT_APPROVE', 'CLOSE')")
    @PostMapping("/{id}/close")
    public ResponseEntity<Response<DocumentItem>> close(@PathVariable Long id) {
        documentActionService.close(id);
        return ResponseEntity.ok(Response.successData(getItemOrThrow(id)));
    }

    @PreAuthorize("hasPermission('PRODUCT_APPROVE', 'CANCEL')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Response<DocumentItem>> cancel(@PathVariable Long id) {
        documentActionService.cancel(id);
        return ResponseEntity.ok(Response.successData(getItemOrThrow(id)));
    }

    private DocumentItem getItemOrThrow(Long id) {
        DocumentItem item = documentRepository.getItemById(id);
        if (item == null) {
            throw new IllegalArgumentException(i18n.msg("error.document.notFound", id));
        }
        return item;
    }
}
