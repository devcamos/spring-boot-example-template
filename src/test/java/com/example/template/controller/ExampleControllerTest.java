package com.example.template.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.template.entity.ExampleEntity;
import com.example.template.service.ExampleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExampleController.class)
class ExampleControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ExampleService service;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void getById_WhenExists_ReturnsEntity() throws Exception {
        ExampleEntity entity = ExampleEntity.builder()
                .id(1L)
                .name("Test Entity")
                .description("Test Description")
                .status("ACTIVE")
                .build();
        
        when(service.findById(1L)).thenReturn(entity);
        
        mockMvc.perform(get("/api/v1/examples/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Entity"));
    }
    
    @Test
    void getAll_ReturnsPaginatedResponse() throws Exception {
        ExampleEntity entity = ExampleEntity.builder()
                .id(1L)
                .name("Test Entity")
                .status("ACTIVE")
                .build();
        
        Page<ExampleEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 20), 1);
        when(service.findAll(any())).thenReturn(com.example.template.dto.PageResponse.of(page));
        
        mockMvc.perform(get("/api/v1/examples"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    void create_WhenValid_ReturnsCreated() throws Exception {
        ExampleEntity entity = ExampleEntity.builder()
                .name("New Entity")
                .description("New Description")
                .status("ACTIVE")
                .build();
        
        ExampleEntity created = ExampleEntity.builder()
                .id(1L)
                .name("New Entity")
                .description("New Description")
                .status("ACTIVE")
                .build();
        
        when(service.create(any(ExampleEntity.class))).thenReturn(created);
        
        mockMvc.perform(post("/api/v1/examples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entity)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Entity"));
    }
}
