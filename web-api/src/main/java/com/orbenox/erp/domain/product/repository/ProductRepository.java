package com.orbenox.erp.domain.product.repository;

import com.orbenox.erp.domain.product.entity.Product;
import com.orbenox.erp.domain.product.projection.ProductItem;
import com.orbenox.erp.domain.product.projection.SimpleProductItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
            WHERE p.deleted = false
            ORDER BY p.id""")
    Slice<ProductItem> getAllItems(Pageable pageable);

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
            WHERE  p.deleted = false
                    AND (LOWER(p.code) LIKE %:search% OR LOWER(p.name) LIKE %:search% OR LOWER(p.description) LIKE %:search% OR LOWER(p.defaultBarcode) LIKE %:search%)
            ORDER BY p.id""")
    Slice<ProductItem> getItemsSearched(Pageable pageable, @Param("search") String search);

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
            WHERE p.deleted = false AND p.enabled = true
            ORDER BY p.id""")
    List<ProductItem> getEnabledItems();

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

    @Query("""
            SELECT p.id as id,
                p.code as code,
                p.name as name,
                p.description as description,
                p.defaultBarcode as defaultBarcode,
                p.enabled as enabled
            FROM Product p
            WHERE p.id = :productId and p.deleted = false""")
    SimpleProductItem getSimpleItemById(@Param("productId") Long productId);
}

