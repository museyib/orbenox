package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.entity.ProductGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductGroupRepository extends JpaRepository<ProductGroup, Long> {
    List<ProductGroup> findAllByDeletedFalse();

    ProductGroup findByIdAndDeletedFalse(Long id);

    List<ProductGroup> findAllByDeletedAndParentId(Boolean deleted, Long parentId);
}
