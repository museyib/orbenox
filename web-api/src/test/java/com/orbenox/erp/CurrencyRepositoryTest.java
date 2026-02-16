package com.orbenox.erp;

import com.orbenox.erp.domain.currency.Currency;
import com.orbenox.erp.domain.currency.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(TestCacheConfig.class)
public class CurrencyRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    CurrencyRepository currencyRepository;

    @Test
    void findByIdAndDeletedFalse_shouldNotReturnDeletedEntity() {
        Currency c = new Currency();
        c.setCode("USD");
        c.setName("Dollar");
        c.setDeleted(true);
        currencyRepository.save(c);

        Currency result = currencyRepository.findByIdAndDeletedFalse(c.getId());

        assertThat(result).isNull();
    }
}
