package com.orbenox.erp.domain.account;

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
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final LocalizationService i18n;

    @PreAuthorize("hasPermission('ACCOUNT', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<AccountItem>>> getAll(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "") String search) {
        Slice<AccountItem> items = accountService.getAllItems(page, size, search);
        Map<String, Object> headers = Map.of("hasNext", items.hasNext(), "hasPrev", items.hasPrevious());
        return ResponseEntity.ok(Response.successDataWithHeaders(items.getContent(), headers));
    }

    @PreAuthorize("hasPermission('ACCOUNT', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<AccountItem>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(accountService.getItemById(id)));
    }

    @PreAuthorize("hasPermission('ACCOUNT', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<AccountItem>> create(@Valid @RequestBody AccountCreateDto dto) {
        return ResponseEntity.ok(Response.successData(accountService.create(dto)));
    }

    @PreAuthorize("hasPermission('ACCOUNT', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<AccountItem>> update(@PathVariable Long id,
                                                        @Valid @RequestBody AccountUpdateDto dto) {
        return ResponseEntity.ok(Response.successData(accountService.update(id, dto)));
    }

    @PreAuthorize("hasPermission('ACCOUNT', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        accountService.softDelete(id);
        var text = i18n.msg("account.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "account.deleted"));
    }
}


