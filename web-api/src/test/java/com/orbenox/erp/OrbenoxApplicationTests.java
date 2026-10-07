package com.orbenox.erp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest(properties = "spring.flyway.enabled=false")
@ActiveProfiles("test")
class OrbenoxApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void main_shouldRunSpringApplication() {
        String[] args = {};
        try (var springApplication = mockStatic(SpringApplication.class)) {
            OrbenoxApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(OrbenoxApplication.class, args));
        }
    }

}
