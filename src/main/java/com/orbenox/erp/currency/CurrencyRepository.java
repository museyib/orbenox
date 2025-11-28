package com.orbenox.erp.currency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency,Long> {
    List<Currency> findAllByDeletedFalse();

    Currency findByIdAndDeletedFalse(Long id);
}
