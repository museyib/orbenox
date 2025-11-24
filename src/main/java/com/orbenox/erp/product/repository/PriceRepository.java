package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Integer> {
}
