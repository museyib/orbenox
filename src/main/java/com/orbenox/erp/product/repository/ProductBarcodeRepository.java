package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.entity.ProductBarcode;
import com.orbenox.erp.product.projection.ProductBarcodeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductBarcodeRepository extends JpaRepository<ProductBarcode, Long> {
    @Query("""
            SELECT b.id as id,
                b.product as product,
                b.unit as unit,
                b.barcode as barcode
            FROM ProductBarcode b
            WHERE b.product.id = :productId
            """)
    List<ProductBarcodeItem> getItemsByProductId(@Param("productId") Long productId);
}
