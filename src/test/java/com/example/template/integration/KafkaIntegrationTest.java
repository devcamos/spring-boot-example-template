package com.example.template.integration;

import com.example.template.event.EventProducer;
import com.example.template.event.ExampleEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"example-events", "example-events-dlq"})
@DirtiesContext
@ActiveProfiles("test")
class KafkaIntegrationTest {
    
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
