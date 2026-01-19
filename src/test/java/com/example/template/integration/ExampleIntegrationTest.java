package com.example.template.integration;

import com.example.template.entity.ExampleEntity;
import com.example.template.repository.ExampleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExampleIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.1-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Autowired
    private ExampleRepository repository;
    
    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }
    
    @Test
    void saveAndFind_ShouldWork() {
        ExampleEntity entity = ExampleEntity.builder()
                .name("Test Entity")
                .description("Test Description")
                .status("ACTIVE")
                .build();
        
        ExampleEntity saved = repository.save(entity);
        
        Optional<ExampleEntity> found = repository.findById(saved.getId());
        
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Entity");
    }
    
    @Test
    void findByStatus_ShouldReturnFilteredResults() {
        ExampleEntity active = ExampleEntity.builder()
                .name("Active Entity")
                .status("ACTIVE")
                .build();
        
        ExampleEntity inactive = ExampleEntity.builder()
                .name("Inactive Entity")
                .status("INACTIVE")
                .build();
        
        repository.save(active);
        repository.save(inactive);
        
        var result = repository.findByStatus("ACTIVE", PageRequest.of(0, 10));
        
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo("ACTIVE");
    }
}
