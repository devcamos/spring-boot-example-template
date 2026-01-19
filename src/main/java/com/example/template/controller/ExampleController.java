package com.example.template.controller;

import com.example.template.dto.PageResponse;
import com.example.template.entity.ExampleEntity;
import com.example.template.service.ExampleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/examples")
@RequiredArgsConstructor
@Tag(name = "Examples", description = "Example entity management API")
public class ExampleController {
    
    private final ExampleService service;
    
    @GetMapping("/{id}")
    @Operation(summary = "Get example by ID")
    public ResponseEntity<ExampleEntity> getById(@PathVariable Long id) {
        log.debug("GET /api/v1/examples/{}", id);
        ExampleEntity entity = service.findById(id);
        return ResponseEntity.ok(entity);
    }
    
    @GetMapping
    @Operation(summary = "Get all examples with pagination")
    public ResponseEntity<PageResponse<ExampleEntity>> getAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        log.debug("GET /api/v1/examples with pagination: {}", pageable);
        PageResponse<ExampleEntity> response = service.findAll(pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search examples")
    public ResponseEntity<PageResponse<ExampleEntity>> search(
            @RequestParam String q,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        log.debug("GET /api/v1/examples/search?q={} with pagination: {}", q, pageable);
        PageResponse<ExampleEntity> response = service.search(q, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get examples by status")
    public ResponseEntity<PageResponse<ExampleEntity>> getByStatus(
            @PathVariable String status,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        log.debug("GET /api/v1/examples/status/{} with pagination: {}", status, pageable);
        PageResponse<ExampleEntity> response = service.findByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @Operation(summary = "Create new example")
    public ResponseEntity<ExampleEntity> create(@Valid @RequestBody ExampleEntity entity) {
        log.debug("POST /api/v1/examples with entity: {}", entity);
        ExampleEntity created = service.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update example")
    public ResponseEntity<ExampleEntity> update(
            @PathVariable Long id,
            @Valid @RequestBody ExampleEntity entity) {
        log.debug("PUT /api/v1/examples/{} with entity: {}", id, entity);
        ExampleEntity updated = service.update(id, entity);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete example")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("DELETE /api/v1/examples/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
