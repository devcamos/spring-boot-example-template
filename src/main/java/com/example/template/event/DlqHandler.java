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
public class DlqHandler {
    
    private static final String DLQ_TOPIC = "example-events-dlq";
    private static final String GROUP_ID = "example-template-group-dlq";
    
    @KafkaListener(topics = DLQ_TOPIC, groupId = GROUP_ID)
    public void handleDlqMessage(
            @Payload ExampleEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp,
            Acknowledgment acknowledgment) {
        
        log.error("DLQ Handler - Processing failed message with key [{}]", key);
        log.error("Event details: {}", event);
        log.error("Original timestamp: {}", timestamp);
        
        // Implement DLQ handling logic:
        // - Log for manual review
        // - Send alert/notification
        // - Attempt reprocessing with exponential backoff
        // - Store in database for analysis
        
        // For now, just acknowledge to remove from DLQ
        acknowledgment.acknowledge();
        log.warn("DLQ message with key [{}] acknowledged and removed from DLQ", key);
    }
}
