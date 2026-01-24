package com.example.template.integration;

import com.example.template.event.EventProducer;
import com.example.template.event.ExampleEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class KafkaIT {

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"))
            .withStartupTimeout(Duration.ofMinutes(2));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        // Disable datasource for Kafka-only test
        registry.add("spring.datasource.url", () -> "");
        registry.add("spring.datasource.driver-class-name", () -> "");
        // Disable micrometer/opentelemetry tracing to avoid OTLP exporter bean creation with empty endpoint
        registry.add("management.tracing.enabled", () -> "false");
    }

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void sendEvent_ShouldSucceed() {
        ExampleEvent event = ExampleEvent.builder()
                .type("TEST_EVENT")
                .payload("Test payload")
                .timestamp(LocalDateTime.now())
                .build();

        // This should not throw an exception
        eventProducer.sendEvent(event);
    }
}
