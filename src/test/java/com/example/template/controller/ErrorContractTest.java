package com.example.template.controller;

import com.example.template.controller.exception.error.ErrorResponse;
import com.example.template.exception.NotFoundException;
import com.example.template.service.ExampleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExampleController.class)
class ErrorContractTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ExampleService service;
    
    @Test
    void errorResponse_ShouldHaveCorrelationId() throws Exception {
        when(service.findById(999L)).thenThrow(new NotFoundException("Entity not found"));
        
        MvcResult result = mockMvc.perform(get("/api/v1/examples/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.correlationId").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/examples/999"))
                .andReturn();
        
        // Verify correlation ID is in response header
        String correlationId = result.getResponse().getHeader("X-Correlation-Id");
        org.junit.jupiter.api.Assertions.assertNotNull(correlationId);
        org.junit.jupiter.api.Assertions.assertFalse(correlationId.isEmpty());
    }
    
    @Test
    void validationError_ShouldIncludeDetails() throws Exception {
        mockMvc.perform(get("/api/v1/examples/search")
                        .param("q", "")) // Empty search term should fail validation
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.correlationId").exists());
    }
}
