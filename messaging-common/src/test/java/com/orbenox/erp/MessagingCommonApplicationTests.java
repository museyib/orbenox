package com.orbenox.erp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MessagingCommonApplicationTests.TestConfig.class)
class MessagingCommonApplicationTests {

    @SpringBootConfiguration
    static class TestConfig {
    }

    @Test
    void contextLoads() {
    }
}
