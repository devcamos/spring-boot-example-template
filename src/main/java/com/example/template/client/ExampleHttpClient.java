package com.example.template.client;

import com.example.template.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExampleHttpClient {
    
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_KEY = "correlationId";
    
    private final WebClient.Builder webClientBuilder;
    
    public <T> Mono<T> get(String url, Class<T> responseType) {
        return buildWebClient()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(responseType)
                .timeout(Duration.ofSeconds(10))
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("External service error: {} - {}", ex.getStatusCode(), ex.getMessage());
                    throw new ExternalServiceException(
                            "External service error: " + ex.getMessage(),
                            ex.getStatusCode().value(),
                            ex
                    );
                })
                .doOnError(Exception.class, ex -> {
                    log.error("Unexpected error calling external service: {}", ex.getMessage(), ex);
                    throw new ExternalServiceException(
                            "Unexpected error calling external service: " + ex.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            ex
                    );
                });
    }
    
    public <T> Mono<T> post(String url, Object requestBody, Class<T> responseType) {
        return buildWebClient()
                .post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .timeout(Duration.ofSeconds(10))
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error("External service error: {} - {}", ex.getStatusCode(), ex.getMessage());
                    throw new ExternalServiceException(
                            "External service error: " + ex.getMessage(),
                            ex.getStatusCode().value(),
                            ex
                    );
                })
                .doOnError(Exception.class, ex -> {
                    log.error("Unexpected error calling external service: {}", ex.getMessage(), ex);
                    throw new ExternalServiceException(
                            "Unexpected error calling external service: " + ex.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            ex
                    );
                });
    }
    
    private WebClient buildWebClient() {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        return webClientBuilder
                .defaultHeader(CORRELATION_ID_HEADER, correlationId != null ? correlationId : "")
                .build();
    }
}
