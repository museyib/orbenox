package com.orbenox.erp.notificationservice;

import com.orbenox.erp.NotificationServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class NotificationServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void main_shouldRunSpringApplication() {
        String[] args = {};
        try (var springApplication = mockStatic(SpringApplication.class)) {
            NotificationServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(NotificationServiceApplication.class, args));
        }
    }

}
