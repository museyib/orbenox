package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> findAllByDeletedFalse();

    Brand findByIdAndDeletedFalse(Long id);
}
