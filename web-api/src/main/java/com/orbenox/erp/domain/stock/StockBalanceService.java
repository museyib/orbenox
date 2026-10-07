package com.orbenox.erp.domain.stock;

import com.orbenox.erp.common.Response;
import com.orbenox.erp.domain.product.projection.ProductItem;
import com.orbenox.erp.domain.product.repository.ProductRepository;
import com.orbenox.erp.domain.warehouse.WarehouseItem;
import com.orbenox.erp.domain.warehouse.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockBalanceService {
    private final StockBalanceClient stockBalanceClient;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public List<StockBalanceItem> getItemsByProductId(Long productId) {
        List<SimpleStockBalanceItem> items = getData(stockBalanceClient.getStockBalanceItems(productId));
        ProductItem productItem = productRepository.getItemById(productId);
        List<Long> warehouseIds = items.stream()
                .map(SimpleStockBalanceItem::warehouseId)
                .toList();
        List<WarehouseItem> warehouseItems = warehouseRepository.findAllByIdIn(warehouseIds);

        Map<Long, WarehouseItem> warehouses = warehouseItems.stream()
                .collect(Collectors.toMap(WarehouseItem::getId, Function.identity()));

        return items.stream()
                .map(balanceItem -> {
                    WarehouseItem warehouseItem = warehouses.get(balanceItem.warehouseId());
                    return createStockBalanceItem(balanceItem, warehouseItem, productItem);
                })
                .toList();
    }

    public StockBalanceItem getStockBalanceItem(Long productId, Long warehouseId) {
        ProductItem productItem = productRepository.getItemById(productId);
        SimpleStockBalanceItem stockBalanceItem = getData(stockBalanceClient.getStockBalanceItem(productId, warehouseId));
        WarehouseItem warehouseItem = warehouseRepository.getItemById(warehouseId);
        return createStockBalanceItem(stockBalanceItem, warehouseItem, productItem);
    }

    private <T> T getData(Response<T> response) {
        if (response == null || !response.isSuccess()) {
            throw new IllegalStateException("Stock service returned an unsuccessful response");
        }
        if (response.getData() == null) {
            throw new IllegalStateException("Stock service returned an empty response");
        }
        return response.getData();
    }

    private StockBalanceItem createStockBalanceItem(SimpleStockBalanceItem balanceItem, WarehouseItem warehouseItem, ProductItem productItem) {
        StockBalanceItem.Unit unit = new StockBalanceItem.Unit(productItem.getDefaultUnit().getId(), productItem.getDefaultUnit().getCode(), productItem.getDefaultUnit().getName(), productItem.getDefaultUnit().isEnabled());
        StockBalanceItem.Product product = new StockBalanceItem.Product(productItem.getId(), unit, productItem.getCode(), productItem.getName(), productItem.getDescription(), productItem.getDefaultBarcode());
        StockBalanceItem.Warehouse warehouse = new StockBalanceItem.Warehouse(warehouseItem.getId(), warehouseItem.getCode(), warehouseItem.getName(), warehouseItem.isEnabled());
        return new StockBalanceItem(
                product,
                warehouse,
                balanceItem.quantity(),
                balanceItem.reservedQuantity(),
                balanceItem.freeQuantity()
        );
    }
}
