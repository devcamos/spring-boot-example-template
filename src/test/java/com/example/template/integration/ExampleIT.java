package com.example.template.integration;

import com.example.template.TestConstants;
import com.example.template.entity.ExampleEntity;
import com.example.template.repository.ExampleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class ExampleIT {

    @MockitoBean
    WebClient.Builder webClientBuilder;

    @SuppressWarnings({ "resource", "java:S2095" })
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.1-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Override Testcontainers-specific database connection properties
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        // Override JPA settings for PostgreSQL in Testcontainers
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
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
                .status(TestConstants.STATUS_ACTIVE)
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
                .status(TestConstants.STATUS_ACTIVE)
                .build();
        
        ExampleEntity inactive = ExampleEntity.builder()
                .name("Inactive Entity")
                .status(TestConstants.STATUS_INACTIVE)
                .build();
        
        repository.save(active);
        repository.save(inactive);
        
        var result = repository.findByStatus(TestConstants.STATUS_ACTIVE, PageRequest.of(0, 10));
        
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(TestConstants.STATUS_ACTIVE);
    }
}
