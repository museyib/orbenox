package com.orbenox.erp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class EurikaServerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void main_shouldRunSpringApplication() {
        String[] args = {};
        try (var springApplication = mockStatic(SpringApplication.class)) {
            EurekaServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(EurekaServiceApplication.class, args));
        }
    }

}
