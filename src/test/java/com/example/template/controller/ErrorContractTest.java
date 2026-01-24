package com.example.template.controller;

import com.example.template.TestConstants;
import com.example.template.exception.NotFoundException;
import com.example.template.service.ExampleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExampleController.class)
class ErrorContractTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private ExampleService service;
    
    @Test
    void errorResponse_ShouldHaveCorrelationId() throws Exception {
        when(service.findById(TestConstants.NON_EXISTENT_ENTITY_ID))
                .thenThrow(new NotFoundException("Entity not found"));
        
        String url = TestConstants.URL_UNDER_TEST + "/" + TestConstants.NON_EXISTENT_ENTITY_ID;
        MvcResult result = mockMvc.perform(get(url))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(TestConstants.JSON_PATH_TIMESTAMP).exists())
                .andExpect(jsonPath(TestConstants.JSON_PATH_STATUS).value(TestConstants.HTTP_NOT_FOUND))
                .andExpect(jsonPath(TestConstants.JSON_PATH_ERROR).exists())
                .andExpect(jsonPath(TestConstants.JSON_PATH_MESSAGE).exists())
                .andExpect(jsonPath(TestConstants.JSON_PATH_CORRELATION_ID).exists())
                .andExpect(jsonPath(TestConstants.JSON_PATH_PATH).value(url))
                .andReturn();
        
        // Verify correlation ID is in response header
        String correlationId = result.getResponse().getHeader(TestConstants.CORRELATION_ID_HEADER);
        assertNotNull(correlationId);
        assertFalse(correlationId.isEmpty());
    }
    
    @Test
    void validationError_ShouldIncludeDetails() throws Exception {
        mockMvc.perform(get(TestConstants.URL_UNDER_TEST + "/search")
                        .param("q", "")) // Empty search term should fail validation
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(TestConstants.JSON_PATH_STATUS).value(TestConstants.HTTP_BAD_REQUEST))
                .andExpect(jsonPath(TestConstants.JSON_PATH_ERROR).exists())
                .andExpect(jsonPath(TestConstants.JSON_PATH_CORRELATION_ID).exists());
    }
}
