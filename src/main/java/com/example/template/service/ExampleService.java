package com.example.template.service;

import com.example.template.dto.PageResponse;
import com.example.template.entity.ExampleEntity;
import com.example.template.exception.BadRequestException;
import com.example.template.exception.ConflictException;
import com.example.template.exception.NotFoundException;
import com.example.template.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExampleService {
    
    private final ExampleRepository repository;
    
    @Cacheable(value = "examples", key = "#id")
    @Transactional(readOnly = true)
    public ExampleEntity findById(Long id) {
        log.debug("Finding example entity by id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Example entity not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public PageResponse<ExampleEntity> findAll(Pageable pageable) {
        log.debug("Finding all example entities with pagination: {}", pageable);
        Page<ExampleEntity> page = repository.findAll(pageable);
        return PageResponse.of(page);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<ExampleEntity> findByStatus(String status, Pageable pageable) {
        log.debug("Finding example entities by status: {} with pagination: {}", status, pageable);
        Page<ExampleEntity> page = repository.findByStatus(status, pageable);
        return PageResponse.of(page);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<ExampleEntity> search(String searchTerm, Pageable pageable) {
        log.debug("Searching example entities with term: {} and pagination: {}", searchTerm, pageable);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new BadRequestException("Search term cannot be empty");
        }
        Page<ExampleEntity> page = repository.search(searchTerm.trim(), pageable);
        return PageResponse.of(page);
    }
    
    public ExampleEntity create(ExampleEntity entity) {
        log.debug("Creating example entity: {}", entity);
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new BadRequestException("Name is required");
        }
        if (repository.existsByName(entity.getName())) {
            throw new ConflictException("Example entity with name '" + entity.getName() + "' already exists");
        }
        return repository.save(entity);
    }
    
    @CacheEvict(value = "examples", key = "#id")
    public ExampleEntity update(Long id, ExampleEntity entity) {
        log.debug("Updating example entity with id: {}", id);
        ExampleEntity existing = findById(id);
        existing.setName(entity.getName());
        existing.setDescription(entity.getDescription());
        existing.setStatus(entity.getStatus());
        return repository.save(existing);
    }
    
    @CacheEvict(value = "examples", key = "#id")
    public void delete(Long id) {
        log.debug("Deleting example entity with id: {}", id);
        ExampleEntity entity = findById(id);
        repository.delete(entity);
    }
}
