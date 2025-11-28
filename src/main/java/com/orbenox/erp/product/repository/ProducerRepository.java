package com.orbenox.erp.product.repository;

import com.orbenox.erp.product.entity.Producer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProducerRepository extends JpaRepository<Producer, Long> {
    List<Producer> findAllByDeletedFalse();

    Producer findByIdAndDeletedFalse(Long id);
}
