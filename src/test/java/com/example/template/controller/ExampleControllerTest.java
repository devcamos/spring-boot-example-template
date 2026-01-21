package com.example.template.controller;

import com.example.template.TestConstants;
import com.example.template.entity.ExampleEntity;
import com.example.template.service.ExampleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExampleController.class)
class ExampleControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private ExampleService service;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    @Test
    void getById_WhenExists_ReturnsEntity() throws Exception {
        ExampleEntity entity = ExampleEntity.builder()
                .id(TestConstants.TEST_ENTITY_ID)
                .name("Test Entity")
                .description("Test Description")
                .status(TestConstants.STATUS_ACTIVE)
                .build();
        
        when(service.findById(TestConstants.TEST_ENTITY_ID)).thenReturn(entity);
        
        String url = TestConstants.URL_UNDER_TEST + "/" + TestConstants.TEST_ENTITY_ID;
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestConstants.JSON_PATH_ID).value(TestConstants.TEST_ENTITY_ID.intValue()))
                .andExpect(jsonPath(TestConstants.JSON_PATH_NAME).value("Test Entity"));
    }
    
    @Test
    void getAll_ReturnsPaginatedResponse() throws Exception {
        ExampleEntity entity = ExampleEntity.builder()
                .id(TestConstants.TEST_ENTITY_ID)
                .name("Test Entity")
                .status(TestConstants.STATUS_ACTIVE)
                .build();
        
        Page<ExampleEntity> page = new PageImpl<>(
                List.of(entity),
                PageRequest.of(0, 20),
                1
        );
        when(service.findAll(any())).thenReturn(com.example.template.dto.PageResponse.of(page));
        
        mockMvc.perform(get(TestConstants.URL_UNDER_TEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestConstants.JSON_PATH_CONTENT).isArray())
                .andExpect(jsonPath(TestConstants.JSON_PATH_TOTAL_ELEMENTS).value(1));
    }
    
    @Test
    void create_WhenValid_ReturnsCreated() throws Exception {
        ExampleEntity entity = ExampleEntity.builder()
                .name("New Entity")
                .description("New Description")
                .status(TestConstants.STATUS_ACTIVE)
                .build();
        
        ExampleEntity created = ExampleEntity.builder()
                .id(TestConstants.TEST_ENTITY_ID)
                .name("New Entity")
                .description("New Description")
                .status(TestConstants.STATUS_ACTIVE)
                .build();
        
        when(service.create(any(ExampleEntity.class))).thenReturn(created);
        
        mockMvc.perform(post(TestConstants.URL_UNDER_TEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entity)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(TestConstants.JSON_PATH_ID).value(TestConstants.TEST_ENTITY_ID.intValue()))
                .andExpect(jsonPath(TestConstants.JSON_PATH_NAME).value("New Entity"));
    }
}
