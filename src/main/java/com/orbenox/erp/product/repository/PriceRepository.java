package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Long> {
    List<Price> findAllByDeletedFalse();

    Price findByIdAndDeletedFalse(Long id);
}
