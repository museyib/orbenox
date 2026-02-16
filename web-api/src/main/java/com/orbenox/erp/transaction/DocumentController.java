package com.orbenox.erp.transaction;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.transaction.command.CreateDocumentCommand;
import com.orbenox.erp.transaction.entity.Document;
import com.orbenox.erp.transaction.projection.DocumentItem;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import com.orbenox.erp.transaction.service.DocumentActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentActionService documentActionService;
    private final DocumentRepository documentRepository;

    @PreAuthorize("hasPermission('DOCUMENT', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<DocumentItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(documentRepository.getAllItems()));
    }

    @PreAuthorize("hasPermission('DOCUMENT', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<DocumentItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(getItemOrThrow(id)));
    }

    @PreAuthorize("hasPermission('DOCUMENT', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<DocumentItem>> create(@RequestBody CreateDocumentCommand command) {
        Document document = documentActionService.createDraft(command);
        return ResponseEntity.ok(Response.successData(getItemOrThrow(document.getId())));
    }

    @PreAuthorize("hasPermission('DOCUMENT', 'UPDATE')")
    @PostMapping("/{id}/submit")
    public ResponseEntity<Response<DocumentItem>> submit(@PathVariable Long id) {
        documentActionService.submit(id);
        return ResponseEntity.ok(Response.successData(getItemOrThrow(id)));
    }

    @PreAuthorize("hasPermission('DOCUMENT', 'UPDATE')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<Response<DocumentItem>> approve(@PathVariable Long id) {
        documentActionService.approve(id);
        return ResponseEntity.ok(Response.successData(getItemOrThrow(id)));
    }

    @PreAuthorize("hasPermission('DOCUMENT', 'UPDATE')")
    @PostMapping("/{id}/post")
    public ResponseEntity<Response<DocumentItem>> post(@PathVariable Long id) {
        documentActionService.post(id);
        return ResponseEntity.ok(Response.successData(getItemOrThrow(id)));
    }

    @PreAuthorize("hasPermission('DOCUMENT', 'UPDATE')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<Response<DocumentItem>> reject(@PathVariable Long id) {
        documentActionService.reject(id);
        return ResponseEntity.ok(Response.successData(getItemOrThrow(id)));
    }

    @PreAuthorize("hasPermission('DOCUMENT', 'UPDATE')")
    @PostMapping("/{id}/close")
    public ResponseEntity<Response<DocumentItem>> close(@PathVariable Long id) {
        documentActionService.close(id);
        return ResponseEntity.ok(Response.successData(getItemOrThrow(id)));
    }

    @PreAuthorize("hasPermission('DOCUMENT', 'UPDATE')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Response<DocumentItem>> cancel(@PathVariable Long id) {
        documentActionService.cancel(id);
        return ResponseEntity.ok(Response.successData(getItemOrThrow(id)));
    }

    private DocumentItem getItemOrThrow(Long id) {
        DocumentItem item = documentRepository.getItemById(id);
        if (item == null) {
            throw new IllegalArgumentException("Document not found: " + id);
        }
        return item;
    }
}

