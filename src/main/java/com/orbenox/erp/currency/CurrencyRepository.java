package com.orbenox.erp.currency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency,Long> {
    List<Currency> findAllByDeleted(Boolean deleted);

    Currency findByIdAndDeleted(Long id, Boolean deleted);
}
