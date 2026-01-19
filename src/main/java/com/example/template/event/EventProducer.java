package com.example.template.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventProducer {
    
    private static final String TOPIC = "example-events";
    private static final String DLQ_TOPIC = "example-events-dlq";
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public void sendEvent(ExampleEvent event) {
        if (event.getId() == null) {
            event.setId(UUID.randomUUID().toString());
        }
        
        String key = event.getId();
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(TOPIC, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent event with key [{}] to topic [{}] with offset [{}]",
                        key, TOPIC, result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send event with key [{}] to topic [{}] due to: {}",
                        key, TOPIC, ex.getMessage(), ex);
                // Send to DLQ
                sendToDlq(event, ex);
            }
        });
    }
    
    private void sendToDlq(ExampleEvent event, Exception originalException) {
        log.error("Sending event [{}] to DLQ topic [{}]", event.getId(), DLQ_TOPIC);
        kafkaTemplate.send(DLQ_TOPIC, event.getId(), event);
    }
}
