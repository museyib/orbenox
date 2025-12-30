package com.orbenox.erp.domain.product.repository;

import com.orbenox.erp.domain.product.entity.ProductBarcode;
import com.orbenox.erp.domain.product.projection.ProductBarcodeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductBarcodeRepository extends JpaRepository<ProductBarcode, Long> {
    @Query("""
            SELECT b.id as id,
                b.product as product,
                b.unit as unit,
                b.barcode as barcode
            FROM ProductBarcode b
            WHERE b.product.id = :productId
            ORDER BY b.id""")
    List<ProductBarcodeItem> getItemsByProductId(@Param("productId") Long productId);

    Optional<ProductBarcode> findByBarcode(String barcode);
}
