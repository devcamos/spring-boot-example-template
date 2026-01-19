package com.example.template.service;

import com.example.template.entity.ExampleEntity;
import com.example.template.exception.BadRequestException;
import com.example.template.exception.ConflictException;
import com.example.template.exception.NotFoundException;
import com.example.template.repository.ExampleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExampleServiceTest {
    
    @Mock
    private ExampleRepository repository;
    
    @InjectMocks
    private ExampleService service;
    
    private ExampleEntity entity;
    
    @BeforeEach
    void setUp() {
        entity = ExampleEntity.builder()
                .id(1L)
                .name("Test Entity")
                .description("Test Description")
                .status("ACTIVE")
                .build();
    }
    
    @Test
    void findById_WhenExists_ReturnsEntity() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        
        ExampleEntity result = service.findById(1L);
        
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Entity");
        verify(repository).findById(1L);
    }
    
    @Test
    void findById_WhenNotExists_ThrowsNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");
    }
    
    @Test
    void create_WhenValid_ReturnsCreatedEntity() {
        when(repository.existsByName("Test Entity")).thenReturn(false);
        when(repository.save(any(ExampleEntity.class))).thenReturn(entity);
        
        ExampleEntity result = service.create(entity);
        
        assertThat(result).isNotNull();
        verify(repository).existsByName("Test Entity");
        verify(repository).save(any(ExampleEntity.class));
    }
    
    @Test
    void create_WhenNameExists_ThrowsConflictException() {
        when(repository.existsByName("Test Entity")).thenReturn(true);
        
        assertThatThrownBy(() -> service.create(entity))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");
    }
    
    @Test
    void create_WhenNameIsEmpty_ThrowsBadRequestException() {
        entity.setName("");
        
        assertThatThrownBy(() -> service.create(entity))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("required");
    }
    
    @Test
    void update_WhenExists_ReturnsUpdatedEntity() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(any(ExampleEntity.class))).thenReturn(entity);
        
        ExampleEntity updated = ExampleEntity.builder()
                .name("Updated Name")
                .description("Updated Description")
                .status("INACTIVE")
                .build();
        
        ExampleEntity result = service.update(1L, updated);
        
        assertThat(result).isNotNull();
        verify(repository).findById(1L);
        verify(repository).save(any(ExampleEntity.class));
    }
    
    @Test
    void delete_WhenExists_DeletesEntity() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        doNothing().when(repository).delete(entity);
        
        service.delete(1L);
        
        verify(repository).findById(1L);
        verify(repository).delete(entity);
    }
}
