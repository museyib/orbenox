package com.orbenox.erp.transaction.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import com.orbenox.erp.transaction.projection.DocumentData;
import com.orbenox.erp.transaction.projection.DocumentItem;
import com.orbenox.erp.transaction.projection.ProductLineItem;
import com.orbenox.erp.transaction.repository.DocumentRepository;
import com.orbenox.erp.transaction.repository.ProductLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentRepository documentRepo;
    private final ProductLineRepository productLineRepo;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('DOCUMENT', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<DocumentItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(documentRepo.getAllItems()));
    }

    @PreAuthorize("hasPermission('DOCUMENT', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<DocumentData>> getById(@PathVariable Long id) {
        DocumentItem doc = getItemOrThrow(id);
        List<ProductLineItem> productLines = productLineRepo.getItemsByDocumentId(doc.getId());
        DocumentData data = new DocumentData(doc, productLines);
        return ResponseEntity.ok(Response.successData(data));
    }

    private DocumentItem getItemOrThrow(Long id) {
        DocumentItem item = documentRepo.getItemById(id);
        if (item == null) {
            throw new IllegalArgumentException(i18n.msg("error.document.notFound", id));
        }
        return item;
    }
}
