package com.orbenox.erp.currency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency,Long> {
    List<Currency> findAllByDeletedFalseOrderByIdAsc();

    Currency findByIdAndDeletedFalse(Long id);
}
