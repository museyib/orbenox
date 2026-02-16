package com.orbenox.erp.domain.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.domain.product.dto.ProductCreateDto;
import com.orbenox.erp.domain.product.dto.ProductUpdateDto;
import com.orbenox.erp.domain.product.projection.ProductItem;
import com.orbenox.erp.domain.product.service.ProductService;
import com.orbenox.erp.localization.LocalizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management operations")
public class ProductController {
    private final ProductService productService;
    private final LocalizationService i18n;

    @Operation(summary = "Get all products", description = "Retrieves a list of all active products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved products",
                    content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasPermission('PRODUCT', 'READ')")
    @GetMapping
    public ResponseEntity<Response<List<ProductItem>>> getAll() {
        return ResponseEntity.ok(Response.successData(productService.getAllItems()));
    }

    @Operation(summary = "Get product by ID", description = "Retrieves a specific product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasPermission('PRODUCT', 'READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<ProductItem>> getById(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(Response.successData(productService.getItemById(id)));
    }

    @Operation(summary = "Create new product", description = "Creates a new product with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasPermission('PRODUCT', 'CREATE')")
    @PostMapping
    public ResponseEntity<Response<ProductItem>> create(@Valid @RequestBody ProductCreateDto dto) {
        return ResponseEntity.ok(Response.successData(productService.create(dto)));
    }

    @Operation(summary = "Update product", description = "Updates an existing product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasPermission('PRODUCT', 'UPDATE')")
    @PatchMapping("/{id}")
    public ResponseEntity<Response<ProductItem>> update(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDto dto) {
        return ResponseEntity.ok(Response.successData(productService.update(id, dto)));
    }

    @Operation(summary = "Delete product", description = "Soft deletes a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasPermission('PRODUCT', 'DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id) {
        productService.softDelete(id);
        var text = i18n.msg("product.deleted", id);
        return ResponseEntity.ok(Response.successMessage(text, "product.deleted"));
    }
}
