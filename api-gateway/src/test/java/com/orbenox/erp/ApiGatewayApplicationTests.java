package com.orbenox.erp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void main_shouldRunSpringApplication() {
        String[] args = {"--spring.main.web-application-type=reactive"};
        try (var springApplication = mockStatic(SpringApplication.class)) {
            ApiGatewayApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(ApiGatewayApplication.class, args));
        }
    }

}
