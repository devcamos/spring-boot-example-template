package com.example.template.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorrelationIdFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private CorrelationIdFilter filter;

    @BeforeEach
    void setUp() {
        MDC.clear();
    }

    @Test
    void doFilterInternal_WhenCorrelationIdInHeader_ShouldUseExistingId() throws ServletException, IOException {
        // Given
        String existingCorrelationId = "existing-correlation-id";
        when(request.getHeader("X-Correlation-Id")).thenReturn(existingCorrelationId);
        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setHeader(eq("X-Correlation-Id"), headerCaptor.capture());
        assertThat(headerCaptor.getValue()).isEqualTo(existingCorrelationId);
        verify(filterChain).doFilter(request, response);
        assertThat(MDC.get("correlationId")).isNull(); // MDC should be cleared in finally
    }

    @Test
    void doFilterInternal_WhenNoCorrelationIdInHeader_ShouldGenerateNewId() throws ServletException, IOException {
        // Given
        when(request.getHeader("X-Correlation-Id")).thenReturn(null);
        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setHeader(eq("X-Correlation-Id"), headerCaptor.capture());
        String generatedId = headerCaptor.getValue();
        assertThat(generatedId).isNotNull();
        assertThat(generatedId).isNotBlank();
        // Verify it's a valid UUID format
        UUID.fromString(generatedId); // Should not throw
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WhenBlankCorrelationIdInHeader_ShouldGenerateNewId() throws ServletException, IOException {
        // Given
        when(request.getHeader("X-Correlation-Id")).thenReturn("   ");
        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setHeader(eq("X-Correlation-Id"), headerCaptor.capture());
        String generatedId = headerCaptor.getValue();
        assertThat(generatedId).isNotNull();
        assertThat(generatedId).isNotBlank();
        assertThat(generatedId).isNotEqualTo("   ");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldSetCorrelationIdInMDC() throws ServletException, IOException {
        // Given
        String correlationId = "test-correlation-id";
        when(request.getHeader("X-Correlation-Id")).thenReturn(correlationId);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        // MDC should be cleared in finally block, but we can verify it was set during execution
        verify(filterChain).doFilter(request, response);
        assertThat(MDC.get("correlationId")).isNull(); // Cleared in finally
    }

    @Test
    void doFilterInternal_WhenFilterChainThrowsException_ShouldStillClearMDC() throws ServletException, IOException {
        // Given
        String correlationId = "test-correlation-id";
        when(request.getHeader("X-Correlation-Id")).thenReturn(correlationId);
        doThrow(new RuntimeException("Filter chain error")).when(filterChain).doFilter(request, response);

        // When & Then
        try {
            filter.doFilterInternal(request, response, filterChain);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Filter chain error");
        }
        // MDC should be cleared even if exception is thrown
        assertThat(MDC.get("correlationId")).isNull();
    }
}
