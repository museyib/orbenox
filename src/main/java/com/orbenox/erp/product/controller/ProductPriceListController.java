package com.orbenox.erp.product.controller;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.product.dto.PriceData;
import com.orbenox.erp.product.dto.ProductPriceListDto;
import com.orbenox.erp.product.request.UpdateProductPriceRequest;
import com.orbenox.erp.product.service.ProductPriceListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/productPriceLists")
public class ProductPriceListController {
    private final ProductPriceListService productPriceListService;

    @GetMapping("/{productId}")
    public ResponseEntity<Response<PriceData>> getAllByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(Response.successData(productPriceListService.getPriceDataByProductId(productId)));
    }

    @GetMapping("/{productId}/byPriceListId")
    public ResponseEntity<Response<ProductPriceListDto>> getProductPriceListByPriceListId(@PathVariable Long productId,
                                                                                          @RequestParam("priceListId") Long priceListId,
                                                                                          @RequestParam("unitId") Long unitId) {
        return ResponseEntity.ok(Response.successData(productPriceListService.findDefaultPriceByProduct(productId, priceListId, unitId)));
    }

    @PostMapping
    public ResponseEntity<Response<PriceData>> create(@RequestBody UpdateProductPriceRequest request) {
        return ResponseEntity.ok(Response.successData(productPriceListService.updateProductPrices(request)));
    }
}
