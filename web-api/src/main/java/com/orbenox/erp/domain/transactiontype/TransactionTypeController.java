package com.orbenox.erp.domain.transactiontype;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.localization.LocalizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactionTypes")
public class TransactionTypeController {
    private final TransactionTypeService transactionTypeService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('TRANSACTION_TYPE', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<TransactionTypeItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(transactionTypeService.getAllItems()));
    }

    @PreAuthorize("hasPermission('TRANSACTION_TYPE', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<TransactionTypeItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(transactionTypeService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('TRANSACTION_TYPE', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<TransactionTypeItem>> create(@Valid @RequestBody TransactionTypeCreateDto dto) {
        return ResponseEntity.ok(Response.successData(transactionTypeService.create(dto)));
    }

    @PreAuthorize("hasPermission('TRANSACTION_TYPE', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<TransactionTypeItem>> update(@PathVariable Long id,
                                                         @Valid @RequestBody TransactionTypeUpdateDto dto) {
        return ResponseEntity.ok(Response.successData(transactionTypeService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('TRANSACTION_TYPE', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        transactionTypeService.softDelete(id);
        var text = i18n.msg("transactionType.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "transactionType.deleted"));
    }
}
