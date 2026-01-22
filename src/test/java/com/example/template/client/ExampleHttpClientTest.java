package com.example.template.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExampleHttpClientTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private ExampleHttpClient httpClient;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestBodySpec requestBodySpec;

    private void setupGetMocks() {
        when(webClientBuilder.defaultHeader(any(), any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.just("response"));
    }

    private void setupPostMocks() {
        when(webClientBuilder.defaultHeader(any(), any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.just("response"));
    }

    @Test
    void buildWebClient_WhenCorrelationIdExists_ShouldIncludeInHeader() {
        // Given
        setupGetMocks();
        String correlationId = "test-correlation-id";
        MDC.put("correlationId", correlationId);

        // When
        httpClient.get("https://api.example.com/data", String.class).block();

        // Then
        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
        verify(webClientBuilder).defaultHeader(eq("X-Correlation-Id"), headerValueCaptor.capture());
        assertThat(headerValueCaptor.getValue()).isEqualTo(correlationId);
        MDC.clear();
    }

    @Test
    void buildWebClient_WhenCorrelationIdMissing_ShouldUseEmptyString() {
        // Given
        setupGetMocks();
        MDC.clear();

        // When
        httpClient.get("https://api.example.com/data", String.class).block();

        // Then
        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
        verify(webClientBuilder).defaultHeader(eq("X-Correlation-Id"), headerValueCaptor.capture());
        assertThat(headerValueCaptor.getValue()).isEmpty();
    }

    @Test
    void buildWebClient_WhenCorrelationIdIsNull_ShouldUseEmptyString() {
        // Given
        setupGetMocks();
        MDC.put("correlationId", null);

        // When
        httpClient.get("https://api.example.com/data", String.class).block();

        // Then
        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
        verify(webClientBuilder).defaultHeader(eq("X-Correlation-Id"), headerValueCaptor.capture());
        assertThat(headerValueCaptor.getValue()).isEmpty();
        MDC.clear();
    }

    @Test
    void post_WhenCorrelationIdExists_ShouldIncludeInHeader() {
        // Given
        setupPostMocks();
        String correlationId = "test-correlation-id";
        MDC.put("correlationId", correlationId);

        // When
        httpClient.post("https://api.example.com/data", "request body", String.class).block();

        // Then
        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
        verify(webClientBuilder, atLeastOnce()).defaultHeader(eq("X-Correlation-Id"), headerValueCaptor.capture());
        assertThat(headerValueCaptor.getValue()).isEqualTo(correlationId);
        verify(webClient).post();
        MDC.clear();
    }

    @Test
    void post_WhenCorrelationIdMissing_ShouldUseEmptyString() {
        // Given
        setupPostMocks();
        MDC.clear();

        // When
        httpClient.post("https://api.example.com/data", "request body", String.class).block();

        // Then
        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);
        verify(webClientBuilder, atLeastOnce()).defaultHeader(eq("X-Correlation-Id"), headerValueCaptor.capture());
        assertThat(headerValueCaptor.getValue()).isEmpty();
        verify(webClient).post();
    }
}
