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
                p_brand as brand,
                p_producer as producer,
                p_productType as productType,
                p_productClass as productClass,
                p_productCategory as productCategory,
                p_productGroup as productGroup,
                p_country as country,
                p_defaultUnit as defaultUnit
            FROM Product p
            LEFT JOIN p.brand as p_brand
            LEFT JOIN p.producer as p_producer
            LEFT JOIN p.productType as p_productType
            LEFT JOIN p.productClass as p_productClass
            LEFT JOIN p.productCategory as p_productCategory
            LEFT JOIN p.productGroup as p_productGroup
            LEFT JOIN p.country as p_country
            LEFT JOIN p.defaultUnit as p_defaultUnit
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
                p_brand as brand,
                p_producer as producer,
                p_productType as productType,
                p_productClass as productClass,
                p_productCategory as productCategory,
                p_productGroup as productGroup,
                p_country as country,
                p_defaultUnit as defaultUnit
            FROM Product p
            LEFT JOIN p.brand as p_brand
            LEFT JOIN p.producer as p_producer
            LEFT JOIN p.productType as p_productType
            LEFT JOIN p.productClass as p_productClass
            LEFT JOIN p.productCategory as p_productCategory
            LEFT JOIN p.productGroup as p_productGroup
            LEFT JOIN p.country as p_country
            LEFT JOIN p.defaultUnit as p_defaultUnit
            WHERE  p.deleted = false
                    AND (LOWER(p.code) LIKE %:search% OR LOWER(p.name) LIKE %:search% OR LOWER(p.description) LIKE %:search% OR LOWER(p.defaultBarcode) LIKE %:search%)
            ORDER BY p.id""")
    Slice<ProductItem> getItemsSearched(Pageable pageable, @Param("search") String search);

    @Query("""
            SELECT p.id as id,
                p.code as code,
                p.name as name,
                p.description as description,
                p.defaultBarcode as defaultBarcode
            FROM Product p
            WHERE p.deleted = false AND p.enabled = true
            ORDER BY p.id""")
    List<SimpleProductItem> getEnabledItems();

    @Query("""
            SELECT p.id as id,
                p.code as code,
                p.name as name,
                p.description as description,
                p.defaultBarcode as defaultBarcode,
                p.enabled as enabled,
                p_brand as brand,
                p_producer as producer,
                p_productType as productType,
                p_productClass as productClass,
                p_productCategory as productCategory,
                p_productGroup as productGroup,
                p_country as country,
                p_defaultUnit as defaultUnit
            FROM Product p
            LEFT JOIN p.brand as p_brand
            LEFT JOIN p.producer as p_producer
            LEFT JOIN p.productType as p_productType
            LEFT JOIN p.productClass as p_productClass
            LEFT JOIN p.productCategory as p_productCategory
            LEFT JOIN p.productGroup as p_productGroup
            LEFT JOIN p.country as p_country
            LEFT JOIN p.defaultUnit as p_defaultUnit
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

