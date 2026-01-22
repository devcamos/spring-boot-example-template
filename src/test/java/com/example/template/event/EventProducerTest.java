package com.example.template.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private EventProducer eventProducer;

    private ExampleEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = ExampleEvent.builder()
                .type("TEST_EVENT")
                .payload("Test payload")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void sendEvent_WhenEventHasId_ShouldUseExistingId() {
        // Given
        String eventId = "existing-id";
        testEvent.setId(eventId);
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));

        when(kafkaTemplate.send(eq("example-events"), eq(eventId), any(ExampleEvent.class)))
                .thenReturn(future);

        // When
        eventProducer.sendEvent(testEvent);

        // Then
        verify(kafkaTemplate).send("example-events", eventId, testEvent);
        assertThat(testEvent.getId()).isEqualTo(eventId);
    }

    @Test
    void sendEvent_WhenEventHasNoId_ShouldGenerateId() {
        // Given
        testEvent.setId(null);
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        when(kafkaTemplate.send(eq("example-events"), keyCaptor.capture(), any(ExampleEvent.class)))
                .thenReturn(future);

        // When
        eventProducer.sendEvent(testEvent);

        // Then
        verify(kafkaTemplate).send(eq("example-events"), any(String.class), eq(testEvent));
        assertThat(testEvent.getId()).isNotNull();
        assertThat(keyCaptor.getValue()).isEqualTo(testEvent.getId());
    }

    @Test
    void sendEvent_WhenSendFails_ShouldSendToDlq() {
        // Given
        String eventId = "test-id";
        testEvent.setId(eventId);
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        RuntimeException exception = new RuntimeException("Kafka error");
        future.completeExceptionally(exception);

        CompletableFuture<SendResult<String, Object>> dlqFuture = new CompletableFuture<>();
        dlqFuture.complete(mock(SendResult.class));

        when(kafkaTemplate.send(eq("example-events"), eq(eventId), any(ExampleEvent.class)))
                .thenReturn(future);
        when(kafkaTemplate.send(eq("example-events-dlq"), eq(eventId), any(ExampleEvent.class)))
                .thenReturn(dlqFuture);

        // When
        eventProducer.sendEvent(testEvent);

        // Then
        verify(kafkaTemplate).send("example-events", eventId, testEvent);
        // Wait a bit for async completion
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        verify(kafkaTemplate).send("example-events-dlq", eventId, testEvent);
    }

    @Test
    void sendEvent_WhenSendSucceeds_ShouldNotSendToDlq() {
        // Given
        String eventId = "test-id";
        testEvent.setId(eventId);
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        SendResult<String, Object> result = mock(SendResult.class);
        future.complete(result);

        when(kafkaTemplate.send(eq("example-events"), eq(eventId), any(ExampleEvent.class)))
                .thenReturn(future);

        // When
        eventProducer.sendEvent(testEvent);

        // Then
        verify(kafkaTemplate).send("example-events", eventId, testEvent);
        verify(kafkaTemplate, never()).send(eq("example-events-dlq"), any(), any());
    }
}
