package com.example.template.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumer {
    
    private static final String TOPIC = "example-events";
    private static final String DLQ_TOPIC = "example-events-dlq";
    private static final String GROUP_ID = "example-template-group";
    
    @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
    public void consume(
            @Payload ExampleEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {
        try {
            log.info("Received event with key [{}]: {}", key, event);
            
            // Process the event
            processEvent(event);
            
            // Acknowledge the message
            acknowledgment.acknowledge();
            log.info("Successfully processed event with key [{}]", key);
            
        } catch (Exception e) {
            log.error("Error processing event with key [{}]: {}", key, e.getMessage(), e);
            // Don't acknowledge - message will be retried or sent to DLQ
            // In a real implementation, you might want to implement retry logic here
        }
    }
    
    @KafkaListener(topics = DLQ_TOPIC, groupId = GROUP_ID + "-dlq")
    public void consumeDlq(
            @Payload ExampleEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            Acknowledgment acknowledgment) {
        log.error("Processing DLQ event with key [{}]: {}", key, event);
        // Handle DLQ events - log, alert, or attempt reprocessing
        acknowledgment.acknowledge();
    }
    
    private void processEvent(ExampleEvent event) {
        // Implement your business logic here
        log.debug("Processing event: {}", event);
    }
}
