package com.orbenox.erp.eventpublisher;

import com.orbenox.erp.EventPublisherApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class EventPublisherApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void main_shouldRunSpringApplication() {
        String[] args = {};
        try (var springApplication = mockStatic(SpringApplication.class)) {
            EventPublisherApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(EventPublisherApplication.class, args));
        }
    }

}
