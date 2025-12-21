package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.entity.Product;
import com.orbenox.erp.product.projection.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByIdAndDeletedFalse(Long id);

    @Query("""
        SELECT p.id as id,
            p.code as code,
            p.name as name,
                p.description as description,
                p.defaultBarcode as defaultBarcode,
                p.enabled as enabled,
                p.brand as brand,
                p.producer as producer,
                p.productType as productType,
                p.productClass as productClass,
                p.productCategory as productCategory,
                p.productGroup as productGroup,
                p.country as country,
                p.defaultUnit as defaultUnit
        FROM Product p
        WHERE p.deleted = false""")
    List<ProductItem> getAllItems();

    @Query("""
            SELECT p.id as id,
                p.code as code,
                p.name as name,
                p.description as description,
                p.defaultBarcode as defaultBarcode,
                p.enabled as enabled,
                p.brand as brand,
                p.producer as producer,
                p.productType as productType,
                p.productClass as productClass,
                p.productCategory as productCategory,
                p.productGroup as productGroup,
                p.country as country,
                p.defaultUnit as defaultUnit
            FROM Product p
            WHERE p.id = :id AND p.deleted = false""")
    ProductItem getItemById(@Param("id") Long id);
}
