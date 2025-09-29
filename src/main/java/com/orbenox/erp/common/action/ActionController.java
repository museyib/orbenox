package com.orbenox.erp.common.action;

import com.orbenox.erp.common.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actions")
@RequiredArgsConstructor
public class ActionController {
    private final ActionService actionService;

    @PreAuthorize("hasPermission('ACION', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<ActionDto>>> getActions() {
        return ResponseEntity.ok(Response.successData(actionService.findAll()));
    }
}
