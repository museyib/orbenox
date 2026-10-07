package com.orbenox.erp.stockservice;

import com.orbenox.erp.StockServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class StockServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void main_shouldRunSpringApplication() {
        String[] args = {};
        try (var springApplication = mockStatic(SpringApplication.class)) {
            StockServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(StockServiceApplication.class, args));
        }
    }

}
